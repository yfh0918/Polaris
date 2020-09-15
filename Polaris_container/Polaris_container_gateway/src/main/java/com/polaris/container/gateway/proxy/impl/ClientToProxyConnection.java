package com.polaris.container.gateway.proxy.impl;

import static com.polaris.container.gateway.proxy.impl.ConnectionState.AWAITING_CHUNK;
import static com.polaris.container.gateway.proxy.impl.ConnectionState.AWAITING_INITIAL;
import static com.polaris.container.gateway.proxy.impl.ConnectionState.DISCONNECT_REQUESTED;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.net.ssl.SSLSession;

import org.apache.commons.lang3.StringUtils;

import com.polaris.container.gateway.pojo.HttpCors;
import com.polaris.container.gateway.pojo.HttpProxy;
import com.polaris.container.gateway.pojo.HttpRequestWrapper;
import com.polaris.container.gateway.proxy.ActivityTracker;
import com.polaris.container.gateway.proxy.FlowContext;
import com.polaris.container.gateway.proxy.FullFlowContext;
import com.polaris.container.gateway.proxy.HttpFilters;
import com.polaris.container.gateway.proxy.HttpFiltersAdapter;
import com.polaris.container.gateway.proxy.SslEngineSource;
import com.polaris.container.gateway.util.ProxyUtils;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.cors.CorsConfigBuilder;
import io.netty.handler.codec.http.cors.CorsHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.handler.traffic.GlobalTrafficShapingHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

/**
 * <p>
 * Represents a connection from a client to our proxy. Each
 * ClientToProxyConnection can have multiple {@link ProxyToServerConnection}s,
 * at most one per outbound host:port.
 * </p>
 * 
 * <p>
 * Once a ProxyToServerConnection has been created for a given server, it is
 * continually reused. The ProxyToServerConnection goes through its own
 * lifecycle of connects and disconnects, with different underlying
 * {@link Channel}s, but only a single ProxyToServerConnection object is used
 * per server. The one exception to this is CONNECT tunneling - if a connection
 * has been used for CONNECT tunneling, that connection will never be reused.
 * </p>
 * 
 * <p>
 * As the ProxyToServerConnections receive responses from their servers, they
 * feed these back to the client by calling
 * {@link #respond(ProxyToServerConnection, HttpFilters, HttpRequest, HttpResponse, HttpObject)}
 * .
 * </p>
 */
public class ClientToProxyConnection extends ProxyConnection<HttpRequestWrapper> {
    /**
     * Used for case-insensitive comparisons when parsing Connection header values.
     */
    private static final String LOWERCASE_TRANSFER_ENCODING_HEADER = HttpHeaderNames.TRANSFER_ENCODING.toString().toLowerCase(Locale.US);

    /**
     * Keep track of all ProxyToServerConnections by remote address.
     */
    private volatile Map<InetSocketAddress, ProxyToServerConnection> serverConnectionsMap = new ConcurrentHashMap<InetSocketAddress, ProxyToServerConnection>();

    /**
     * Keep track of how many servers are currently in the process of
     * connecting.
     */
    private final AtomicInteger numberOfCurrentlyConnectingServers = new AtomicInteger(
            0);

    /**
     * This is the current server connection that we're using while transferring
     * chunked data.
     */
    private volatile ProxyToServerConnection currentServerConnection;

    /**
     * The current filters to apply to incoming requests/chunks.
     */
    private volatile HttpFilters currentFilters = HttpFiltersAdapter.NOOP_FILTER;

    private volatile SSLSession clientSslSession;

    private final GlobalTrafficShapingHandler globalTrafficShapingHandler;

    public ClientToProxyConnection(
            final DefaultHttpProxyServer proxyServer,
            SslEngineSource sslEngineSource,
            ChannelPipeline pipeline,
            GlobalTrafficShapingHandler globalTrafficShapingHandler) {
        super(AWAITING_INITIAL, proxyServer);

        initChannelPipeline(pipeline);

        if (sslEngineSource != null) {
            LOG.debug("Enabling encryption of traffic from client to proxy");
            encrypt(pipeline, sslEngineSource.newSslEngine(pipeline.channel().alloc()))
                    .addListener(
                            new GenericFutureListener<Future<? super Channel>>() {
                                @Override
                                public void operationComplete(
                                        Future<? super Channel> future)
                                        throws Exception {
                                    if (future.isSuccess()) {
                                        clientSslSession = sslEngine
                                                .getSession();
                                        recordClientSSLHandshakeSucceeded();
                                    }
                                }
                            });
        }
        this.globalTrafficShapingHandler = globalTrafficShapingHandler;

        LOG.debug("Created ClientToProxyConnection");
    }

    /***************************************************************************
     * Reading
     **************************************************************************/

    @Override
    protected ConnectionState readHTTPInitial(HttpRequestWrapper httpRequest) {
        LOG.debug("Received raw request: {}", httpRequest);

        // if we cannot parse the request, immediately return a 400 and close the connection, since we do not know what state
        // the client thinks the connection is in
        if (httpRequest.decoderResult().isFailure()) {
            LOG.debug("Could not parse request from client. Decoder result: {}", httpRequest.decoderResult().toString());
            FullHttpResponse response = ProxyUtils.createFullHttpResponse(HttpVersion.HTTP_1_1,
                    HttpResponseStatus.BAD_REQUEST,
                    "Unable to parse HTTP request");
            HttpUtil.setKeepAlive(response, false);
            respondWithShortCircuitResponse(response);
            return DISCONNECT_REQUESTED;
        }
        return doReadHTTPInitial(httpRequest);
    }

    /**
     * <p>
     * Reads an {@link HttpRequest}.
     * </p>
     * 
     * <p>
     * If we don't yet have a {@link ProxyToServerConnection} for the desired
     * server, this takes care of creating it.
     * </p>
     * 
     * <p>
     * Note - the "server" could be a chained proxy, not the final endpoint for
     * the request.
     * </p>
     * 
     * @param httpRequest
     * @return
     */
    private ConnectionState doReadHTTPInitial(HttpRequestWrapper httpRequest) {
        
        // Set up our filters based on the original request. If the HttpFiltersSource returns null (meaning the request/response
        // should not be filtered), fall back to the default no-op filter source.
        HttpFilters filterInstance = proxyServer.getFiltersSource().filterRequest(httpRequest, ctx);
        if (filterInstance != null) {
            currentFilters = filterInstance;
        } else {
            currentFilters = HttpFiltersAdapter.NOOP_FILTER;
        }
        
        // Send the request through the clientToProxyRequest filter, and respond with the short-circuit response if required
        HttpResponse clientToProxyFilterResponse = currentFilters.clientToProxyRequest(httpRequest);

        if (clientToProxyFilterResponse != null) {
            LOG.debug("Responding to client with short-circuit response from filter: {}", clientToProxyFilterResponse);
            boolean keepAlive = respondWithShortCircuitResponse(clientToProxyFilterResponse);
            if (keepAlive) {
                return AWAITING_INITIAL;
            } else {
                return DISCONNECT_REQUESTED;
            }
        }

        // Identify our server and chained proxy
        String serverHostAndPort = httpRequest.getServerHostAndPort();
        LOG.debug("Ensuring that hostAndPort are available in {}",
                httpRequest.uri());
        if (serverHostAndPort == null || StringUtils.isBlank(serverHostAndPort)) {
            LOG.warn("No host and port found in {}", httpRequest.uri());
            boolean keepAlive = writeBadGateway(httpRequest);
            if (keepAlive) {
                return AWAITING_INITIAL;
            } else {
                return DISCONNECT_REQUESTED;
            }
        }
        
        //get remote address
        InetSocketAddress remoteAddress = null;
        try {
            remoteAddress = addressFor(httpRequest.getHttpProxy(), proxyServer);
        } catch (UnknownHostException uhe) {
            LOG.info("Bad Host {}", serverHostAndPort + httpRequest.uri());
            boolean keepAlive = writeBadGateway(httpRequest);
            resumeReading();
            if (keepAlive) {
                return AWAITING_INITIAL;
            } else {
                return DISCONNECT_REQUESTED;
            }
        }
        

        String strRemoteAddress = remoteAddress.toString();
        LOG.debug("Finding ProxyToServerConnection remoteAddress for :{}, serverHostAndPort for: {}", strRemoteAddress, serverHostAndPort);
        currentServerConnection = this.serverConnectionsMap.get(remoteAddress.toString());
        if (currentServerConnection == null) {
            try {
                currentServerConnection = ProxyToServerConnection.create(
                        proxyServer,
                        this,
                        currentFilters,
                        remoteAddress,
                        globalTrafficShapingHandler);
                if (currentServerConnection == null) {
                    LOG.debug("Unable to create server connection, probably no chained proxies available");
                    boolean keepAlive = writeBadGateway(httpRequest);
                    resumeReading();
                    if (keepAlive) {
                        return AWAITING_INITIAL;
                    } else {
                        return DISCONNECT_REQUESTED;
                    }
                }
                // Remember the connection for later
                serverConnectionsMap.put(remoteAddress, currentServerConnection);
            } catch (UnknownHostException uhe) {
                LOG.info("Bad Host {}", serverHostAndPort + httpRequest.uri());
                boolean keepAlive = writeBadGateway(httpRequest);
                resumeReading();
                if (keepAlive) {
                    return AWAITING_INITIAL;
                } else {
                    return DISCONNECT_REQUESTED;
                }
            }
        } else {
            LOG.debug("Reusing existing server connection: {}",
                    currentServerConnection);
        }

        modifyRequestHeadersToReflectProxying(httpRequest);

        HttpResponse proxyToServerFilterResponse = currentFilters.proxyToServerRequest(httpRequest);
        if (proxyToServerFilterResponse != null) {
            LOG.debug("Responding to client with short-circuit response from filter: {}", proxyToServerFilterResponse);

            boolean keepAlive = respondWithShortCircuitResponse(proxyToServerFilterResponse);
            if (keepAlive) {
                return AWAITING_INITIAL;
            } else {
                return DISCONNECT_REQUESTED;
            }
        }

        LOG.debug("Writing request to ProxyToServerConnection");
        currentServerConnection.write(httpRequest, currentFilters);

        // Figure out our next state
        if (ProxyUtils.isChunked(httpRequest)) {
            return AWAITING_CHUNK;
        } else {
            return AWAITING_INITIAL;
        }
    }
    
    @Override
    protected void readHTTPChunk(HttpContent chunk) {
        currentFilters.clientToProxyRequest(chunk);
        currentFilters.proxyToServerRequest(chunk);
        currentServerConnection.write(chunk);
    }

    /***************************************************************************
     * Writing
     **************************************************************************/

    /**
     * Send a response to the client.
     * 
     * @param serverConnection
     *            the ProxyToServerConnection that's responding
     * @param filters
     *            the filters to apply to the response
     * @param currentHttpRequest
     *            the HttpRequest that prompted this response
     * @param currentHttpResponse
     *            the HttpResponse corresponding to this data (when doing
     *            chunked transfers, this is the initial HttpResponse object
     *            that came in before the other chunks)
     * @param httpObject
     *            the data with which to respond
     */
    void respond(ProxyToServerConnection serverConnection, HttpFilters filters,
            HttpRequest currentHttpRequest, HttpResponse currentHttpResponse,
            HttpObject httpObject) {
        httpObject = filters.serverToProxyResponse(httpObject);
        if (httpObject == null) {
            forceDisconnect(serverConnection);
            return;
        }

        if (httpObject instanceof HttpResponse) {
            HttpResponse httpResponse = (HttpResponse) httpObject;

            // if this HttpResponse does not have any means of signaling the end of the message body other than closing
            // the connection, convert the message to a "Transfer-Encoding: chunked" HTTP response. This avoids the need
            // to close the client connection to indicate the end of the message. (Responses to HEAD requests "must be" empty.)
            if (!ProxyUtils.isHEAD(currentHttpRequest) && !ProxyUtils.isResponseSelfTerminating(httpResponse)) {
                // if this is not a FullHttpResponse,  duplicate the HttpResponse from the server before sending it to
                // the client. this allows us to set the Transfer-Encoding to chunked without interfering with netty's
                // handling of the response from the server. if we modify the original HttpResponse from the server,
                // netty will not generate the appropriate LastHttpContent when it detects the connection closure from
                // the server (see HttpObjectDecoder#decodeLast). (This does not apply to FullHttpResponses, for which
                // netty already generates the empty final chunk when Transfer-Encoding is chunked.)
                if (!(httpResponse instanceof FullHttpResponse)) {
                    HttpResponse duplicateResponse = ProxyUtils.duplicateHttpResponse(httpResponse);

                    // set the httpObject and httpResponse to the duplicated response, to allow all other standard processing
                    // (filtering, header modification for proxying, etc.) to be applied.
                    httpObject = httpResponse = duplicateResponse;
                }
                HttpUtil.setTransferEncodingChunked(httpResponse, true);
            }

            fixHttpVersionHeaderIfNecessary(httpResponse);
            modifyResponseHeadersToReflectProxying(httpResponse);
        }

        httpObject = filters.proxyToClientResponse(httpObject);
        if (httpObject == null) {
            forceDisconnect(serverConnection);
            return;
        }

        write(httpObject);

        if (ProxyUtils.isLastChunk(httpObject)) {
            writeEmptyBuffer();
        }

        closeConnectionsAfterWriteIfNecessary(serverConnection,
                currentHttpRequest, currentHttpResponse, httpObject);
    }

    /***************************************************************************
     * Connection Lifecycle
     **************************************************************************/

    /**
     * On connect of the client, start waiting for an initial
     * {@link HttpRequest}.
     */
    @Override
    protected void connected() {
        super.connected();
        become(AWAITING_INITIAL);
        recordClientConnected();
    }

    void timedOut(ProxyToServerConnection serverConnection) {
        if (currentServerConnection == serverConnection && this.lastReadTime > currentServerConnection.lastReadTime) {
            // the idle timeout fired on the active server connection. send a timeout response to the client.
            LOG.warn("Server timed out: {}", currentServerConnection);
            currentFilters.serverToProxyResponseTimedOut();
            writeGatewayTimeout();
        }
    }

    @Override
    protected void timedOut() {
        // idle timeout fired on the client channel. if we aren't waiting on a response from a server, hang up
        if (currentServerConnection == null || this.lastReadTime <= currentServerConnection.lastReadTime) {
            super.timedOut();
        }
    }

    /**
     * On disconnect of the client, disconnect all server connections.
     */
    @Override
    protected void disconnected() {
        super.disconnected();
        Iterator<Map.Entry<InetSocketAddress, ProxyToServerConnection>> it = serverConnectionsMap.entrySet().iterator();  
        while(it.hasNext()){  
            Map.Entry<InetSocketAddress, ProxyToServerConnection> entry = it.next();  
            entry.getValue().disconnect();
        } 
        recordClientDisconnected();
    }

    /**
     * Called when {@link ProxyToServerConnection} starts its connection flow.
     * 
     * @param serverConnection
     */
    protected void serverConnectionFlowStarted(
            ProxyToServerConnection serverConnection) {
        stopReading();
        this.numberOfCurrentlyConnectingServers.incrementAndGet();
    }

    /**
     * If the {@link ProxyToServerConnection} completes its connection lifecycle
     * successfully, this method is called to let us know about it.
     * 
     * @param serverConnection
     * @param shouldForwardInitialRequest
     */
    protected void serverConnectionSucceeded(
            ProxyToServerConnection serverConnection,
            boolean shouldForwardInitialRequest) {
        LOG.debug("Connection to server succeeded: {}",
                serverConnection.getRemoteAddress());
        resumeReadingIfNecessary();
        become(shouldForwardInitialRequest ? getCurrentState()
                : AWAITING_INITIAL);
    }

    /**
     * If the {@link ProxyToServerConnection} fails to complete its connection
     * lifecycle successfully, this method is called to let us know about it.
     * 
     * <p>
     * After failing to connect to the server, one of two things can happen:
     * </p>
     * 
     * <ol>
     * <li>If the server was a chained proxy, we fall back to connecting to the
     * ultimate endpoint directly.</li>
     * <li>If the server was the ultimate endpoint, we return a 502 Bad Gateway
     * to the client.</li>
     * </ol>
     * 
     * @param serverConnection
     * @param lastStateBeforeFailure
     * @param cause
     *            what caused the failure
     * 
     * @return true if we're falling back to a another chained proxy (or direct
     *         connection) and trying again
     */
    protected boolean serverConnectionFailed(
            ProxyToServerConnection serverConnection,
            ConnectionState lastStateBeforeFailure,
            Throwable cause) {
        resumeReadingIfNecessary();
        HttpRequestWrapper currentHttpRequest = serverConnection.getCurrentHttpRequest();
        try {
            boolean retrying = serverConnection.connectionFailed(cause);
            if (retrying) {
                LOG.debug("Failed to connect to upstream server or chained proxy. Retrying connection. Last state before failure: {}",
                        lastStateBeforeFailure, cause);
                return true;
            } else {
                LOG.debug(
                        "Connection to upstream server or chained proxy failed: {}.  Last state before failure: {}",
                        serverConnection.getRemoteAddress(),
                        lastStateBeforeFailure,
                        cause);
                connectionFailedUnrecoverably(currentHttpRequest, serverConnection);
                return false;
            }
        } catch (UnknownHostException uhe) {
            connectionFailedUnrecoverably(currentHttpRequest, serverConnection);
            return false;
        }
    }
    
    private void connectionFailedUnrecoverably(HttpRequestWrapper initialRequest, ProxyToServerConnection serverConnection) {
        // the connection to the server failed, so disconnect the server and remove the ProxyToServerConnection from the
        // map of open server connections
        serverConnection.disconnect();
        this.serverConnectionsMap.remove(serverConnection.getRemoteAddress().toString());
        boolean keepAlive = writeBadGateway(initialRequest);
        if (keepAlive) {
            become(AWAITING_INITIAL);
        } else {
            become(DISCONNECT_REQUESTED);
        }
    }

    private void resumeReadingIfNecessary() {
        if (this.numberOfCurrentlyConnectingServers.decrementAndGet() == 0) {
            LOG.debug("All servers have finished attempting to connect, resuming reading from client.");
            resumeReading();
        }
    }

    /***************************************************************************
     * Other Lifecycle
     **************************************************************************/

    /**
     * On disconnect of the server, track that we have one fewer connected
     * servers and then disconnect the client if necessary.
     * 
     * @param serverConnection
     */
    protected void serverDisconnected(ProxyToServerConnection serverConnection) {
        serverConnectionsMap.remove(serverConnection.getRemoteAddress());
    }

    /**
     * When the ClientToProxyConnection becomes saturated, stop reading on all
     * associated ProxyToServerConnections.
     */
    @Override
    synchronized protected void becameSaturated() {
        super.becameSaturated();
        Iterator<Map.Entry<InetSocketAddress, ProxyToServerConnection>> it = serverConnectionsMap.entrySet().iterator();  
        while(it.hasNext()){  
            Map.Entry<InetSocketAddress, ProxyToServerConnection> entry = it.next();  
            entry.getValue().stopReading();
        } 
   }

    /**
     * When the ClientToProxyConnection becomes writable, resume reading on all
     * associated ProxyToServerConnections.
     */
    @Override
    synchronized protected void becameWritable() {
        super.becameWritable();
        Iterator<Map.Entry<InetSocketAddress, ProxyToServerConnection>> it = serverConnectionsMap.entrySet().iterator();  
        while(it.hasNext()){  
            Map.Entry<InetSocketAddress, ProxyToServerConnection> entry = it.next();  
            synchronized (entry.getValue()) {
                if (!this.isSaturated()) {
                    entry.getValue().resumeReading();
                }
            }
        } 
    }

    /**
     * When a server becomes saturated, we stop reading from the client.
     * 
     * @param serverConnection
     */
    synchronized protected void serverBecameSaturated(ProxyToServerConnection serverConnection) {
        if (serverConnection.isSaturated()) {
            LOG.info("Connection to server became saturated, stopping reading");
            stopReading();
        }
    }

    /**
     * When a server becomes writeable, we check to see if all servers are
     * writeable and if they are, we resume reading.
     * 
     * @param serverConnection
     */
    synchronized protected void serverBecameWriteable(
            ProxyToServerConnection serverConnection) {
        boolean anyServersSaturated = false;
        Iterator<Map.Entry<InetSocketAddress, ProxyToServerConnection>> it = serverConnectionsMap.entrySet().iterator();  
        while(it.hasNext()){  
            Map.Entry<InetSocketAddress, ProxyToServerConnection> entry = it.next();  
            if (entry.getValue().isSaturated()) {
                anyServersSaturated = true;
                break;
            }
        } 
        if (!anyServersSaturated) {
            LOG.info("All server connections writeable, resuming reading");
            resumeReading();
        }
    }

    @Override
    protected void exceptionCaught(Throwable cause) {
        try {
            if (cause instanceof IOException) {
                // IOExceptions are expected errors, for example when a browser is killed and aborts a connection.
                // rather than flood the logs with stack traces for these expected exceptions, we log the message at the
                // INFO level and the stack trace at the DEBUG level.
                LOG.info("An IOException occurred on ClientToProxyConnection: " + cause.getMessage());
                LOG.debug("An IOException occurred on ClientToProxyConnection", cause);
            } else if (cause instanceof RejectedExecutionException) {
                LOG.info("An executor rejected a read or write operation on the ClientToProxyConnection (this is normal if the proxy is shutting down). Message: " + cause.getMessage());
                LOG.debug("A RejectedExecutionException occurred on ClientToProxyConnection", cause);
            } else {
                LOG.error("Caught an exception on ClientToProxyConnection", cause);
            }
        } finally {
            // always disconnect the client when an exception occurs on the channel
            disconnect();
        }
    }

    /***************************************************************************
     * Connection Management
     **************************************************************************/

    /**
     * Initialize the {@link ChannelPipeline} for the client to proxy channel.
     * LittleProxy acts like a server here.
     * 
     * A {@link ChannelPipeline} invokes the read (Inbound) handlers in
     * ascending ordering of the list and then the write (Outbound) handlers in
     * descending ordering.
     * 
     * Regarding the Javadoc of {@link HttpObjectAggregator} it's needed to have
     * the {@link HttpResponseEncoder} or {@link io.netty.handler.codec.http.HttpRequestEncoder} before the
     * {@link HttpObjectAggregator} in the {@link ChannelPipeline}.
     * 
     * @param pipeline
     */
    protected void initChannelPipeline(ChannelPipeline pipeline) {
        LOG.debug("Configuring ChannelPipeline");
        pipeline.addLast("encoder", new HttpResponseEncoder());
        pipeline.addLast("decoder", new HttpRequestDecoder(
                proxyServer.getMaxInitialLineLength(),
                proxyServer.getMaxHeaderSize(),
                proxyServer.getMaxChunkSize()));
        pipeline.addLast("requestReadMonitor", requestReadMonitor);
        pipeline.addLast("responseWrittenMonitor", responseWrittenMonitor);
        pipeline.addLast(
                "idle",
                new IdleStateHandler(0, 0, proxyServer
                        .getIdleConnectionTimeout()));
        pipeline.addLast("chunkedWriteHandler", new ChunkedWriteHandler());
        pipeline.addLast(ClientToProxyConnectionBefore.NAME, new ClientToProxyConnectionBefore());
        if (HttpCors.isEnable()) {
            createCorsHandler(pipeline);
        }
        pipeline.addLast("handler", this);
    }
    
    private void createCorsHandler(ChannelPipeline pipeline) {
        
        //orgin
        String[] allowOrigin = new String[HttpCors.getAllowOrigin().size()];
        HttpCors.getAllowOrigin().toArray(allowOrigin);
        
        //headers
        String[] allowHeaders = new String[HttpCors.getAllowHeaders().size()];
        HttpCors.getAllowHeaders().toArray(allowHeaders);
        
        //methods
        HttpMethod[] allowMethods = new HttpMethod[HttpCors.getAllowMethods().size()];
        HttpCors.getAllowMethods().toArray(allowMethods);
        
        //exposeHeaders
        String[] exposeHeaders = new String[HttpCors.getExposeHeaders().size()];
        HttpCors.getExposeHeaders().toArray(exposeHeaders);
        
        //builder
        CorsConfigBuilder corsConfigBuilder = null;
        if (allowOrigin.length == 1) {
            corsConfigBuilder = CorsConfigBuilder.forOrigin(allowOrigin[0]);
        } else {
            corsConfigBuilder = CorsConfigBuilder.forOrigins(allowOrigin);
        }
        corsConfigBuilder.allowNullOrigin()
                         .allowedRequestHeaders(allowHeaders)
                         .allowedRequestMethods(allowMethods)
                         .exposeHeaders(exposeHeaders)
                         .maxAge(HttpCors.getMaxAge());
        if (HttpCors.allowCredentials()) {
            corsConfigBuilder.allowCredentials();
        }
        pipeline.addLast(new CorsHandler(corsConfigBuilder.build()));
    }

    /**
     * This method takes care of closing client to proxy and/or proxy to server
     * connections after finishing a write.
     */
    private void closeConnectionsAfterWriteIfNecessary(
            ProxyToServerConnection serverConnection,
            HttpRequest currentHttpRequest, HttpResponse currentHttpResponse,
            HttpObject httpObject) {
        boolean closeServerConnection = shouldCloseServerConnection(
                currentHttpRequest, currentHttpResponse, httpObject);
        boolean closeClientConnection = shouldCloseClientConnection(
                currentHttpRequest, currentHttpResponse, httpObject);

        if (closeServerConnection) {
            LOG.debug("Closing remote connection after writing to client");
            serverConnection.disconnect();
        }

        if (closeClientConnection) {
            LOG.debug("Closing connection to client after writes");
            disconnect();
        }
    }

    private void forceDisconnect(ProxyToServerConnection serverConnection) {
        LOG.debug("Forcing disconnect");
        serverConnection.disconnect();
        disconnect();
    }

    /**
     * Determine whether or not the client connection should be closed.
     * 
     * @param req
     * @param res
     * @param httpObject
     * @return
     */
    private boolean shouldCloseClientConnection(HttpRequest req,
            HttpResponse res, HttpObject httpObject) {
        if (ProxyUtils.isChunked(res)) {
            // If the response is chunked, we want to return false unless it's
            // the last chunk. If it is the last chunk, then we want to pass
            // through to the same close semantics we'd otherwise use.
            if (httpObject != null) {
                if (!ProxyUtils.isLastChunk(httpObject)) {
                    String uri = null;
                    if (req != null) {
                        uri = req.uri();
                    }
                    LOG.debug("Not closing client connection on middle chunk for {}", uri);
                    return false;
                } else {
                    LOG.debug("Handling last chunk. Using normal client connection closing rules.");
                }
            }
        }

        if (!HttpUtil.isKeepAlive(req)) {
            LOG.debug("Closing client connection since request is not keep alive: {}", req);
            // Here we simply want to close the connection because the
            // client itself has requested it be closed in the request.
            return true;
        }

        // ignore the response's keep-alive; we can keep this client connection open as long as the client allows it.

        LOG.debug("Not closing client connection for request: {}", req);
        return false;
    }

    /**
     * Determines if the remote connection should be closed based on the request
     * and response pair. If the request is HTTP 1.0 with no keep-alive header,
     * for example, the connection should be closed.
     * 
     * This in part determines if we should close the connection. Here's the
     * relevant section of RFC 2616:
     * 
     * "HTTP/1.1 defines the "close" connection option for the sender to signal
     * that the connection will be closed after completion of the response. For
     * example,
     * 
     * Connection: close
     * 
     * in either the request or the response header fields indicates that the
     * connection SHOULD NOT be considered `persistent' (section 8.1) after the
     * current request/response is complete."
     * 
     * @param req
     *            The request.
     * @param res
     *            The response.
     * @param msg
     *            The message.
     * @return Returns true if the connection should close.
     */
    private boolean shouldCloseServerConnection(HttpRequest req,
            HttpResponse res, HttpObject msg) {
        if (ProxyUtils.isChunked(res)) {
            // If the response is chunked, we want to return false unless it's
            // the last chunk. If it is the last chunk, then we want to pass
            // through to the same close semantics we'd otherwise use.
            if (msg != null) {
                if (!ProxyUtils.isLastChunk(msg)) {
                    String uri = null;
                    if (req != null) {
                        uri = req.uri();
                    }
                    LOG.debug("Not closing server connection on middle chunk for {}", uri);
                    return false;
                } else {
                    LOG.debug("Handling last chunk. Using normal server connection closing rules.");
                }
            }
        }

        // ignore the request's keep-alive; we can keep this server connection open as long as the server allows it.

        if (!HttpUtil.isKeepAlive(res)) {
            LOG.debug("Closing server connection since response is not keep alive: {}", res);
            // In this case, we want to honor the Connection: close header
            // from the remote server and close that connection. We don't
            // necessarily want to close the connection to the client, however
            // as it's possible it has other connections open.
            return true;
        }

        LOG.debug("Not closing server connection for response: {}", res);
        return false;
    }

    /***************************************************************************
     * Request/Response Rewriting
     **************************************************************************/

    /**
     * Chunked encoding is an HTTP 1.1 feature, but sometimes we get a chunked
     * response that reports its HTTP version as 1.0. In this case, we change it
     * to 1.1.
     * 
     * @param httpResponse
     */
    private void fixHttpVersionHeaderIfNecessary(HttpResponse httpResponse) {
        String te = httpResponse.headers().get(
        		HttpHeaderNames.TRANSFER_ENCODING.toString());
        if (StringUtils.isNotBlank(te)
                && te.equalsIgnoreCase(HttpHeaderValues.CHUNKED.toString())) {
            if (httpResponse.protocolVersion() != HttpVersion.HTTP_1_1) {
                LOG.debug("Fixing HTTP version.");
                httpResponse.setProtocolVersion(HttpVersion.HTTP_1_1);
            }
        }
    }

    /**
     * If and only if our proxy is not running in transparent mode, modify the
     * request headers to reflect that it was proxied.
     * 
     * @param httpRequest
     */
    private void modifyRequestHeadersToReflectProxying(HttpRequest httpRequest) {
        /*
         * We are making the request to the origin server, so must modify
         * the 'absolute-URI' into the 'origin-form' as per RFC 7230
         * section 5.3.1.
         *
         * This must happen even for 'transparent' mode, otherwise the origin
         * server could infer that the request came via a proxy server.
         */
        LOG.debug("Modifying request for proxy chaining");
        // Strip host from uri
        String uri = httpRequest.uri();
        String adjustedUri = ProxyUtils.stripHost(uri);
        LOG.debug("Stripped host from uri: {}    yielding: {}", uri,
                adjustedUri);
        httpRequest.setUri(adjustedUri);
        
        if (!proxyServer.isTransparent()) {
            LOG.debug("Modifying request headers for proxying");

            HttpHeaders headers = httpRequest.headers();

            // Remove sdch from encodings we accept since we can't decode it.
            ProxyUtils.removeSdchEncoding(headers);
            switchProxyConnectionHeader(headers);
            stripConnectionTokens(headers);
            stripHopByHopHeaders(headers);
            ProxyUtils.addVia(httpRequest, proxyServer.getProxyAlias());
        }
    }

    /**
     * If and only if our proxy is not running in transparent mode, modify the
     * response headers to reflect that it was proxied.
     * 
     * @param httpResponse
     * @return
     */
    private void modifyResponseHeadersToReflectProxying(
            HttpResponse httpResponse) {
        if (!proxyServer.isTransparent()) {
            HttpHeaders headers = httpResponse.headers();

            stripConnectionTokens(headers);
            stripHopByHopHeaders(headers);
            ProxyUtils.addVia(httpResponse, proxyServer.getProxyAlias());

            /*
             * RFC2616 Section 14.18
             * 
             * A received message that does not have a Date header field MUST be
             * assigned one by the recipient if the message will be cached by
             * that recipient or gatewayed via a protocol which requires a Date.
             */
            if (!headers.contains(HttpHeaderNames.DATE.toString())) {
                httpResponse.headers().set(HttpHeaderNames.DATE, new Date());
            }
        }
    }

    /**
     * Switch the de-facto standard "Proxy-Connection" header to "Connection"
     * when we pass it along to the remote host. This is largely undocumented
     * but seems to be what most browsers and servers expect.
     * 
     * @param headers
     *            The headers to modify
     */
    private void switchProxyConnectionHeader(HttpHeaders headers) {
        String proxyConnectionKey = "Proxy-Connection";
        if (headers.contains(proxyConnectionKey)) {
            String header = headers.get(proxyConnectionKey);
            headers.remove(proxyConnectionKey);
            headers.set(HttpHeaderNames.CONNECTION.toString(), header);
        }
    }

    /**
     * RFC2616 Section 14.10
     * 
     * HTTP/1.1 proxies MUST parse the Connection header field before a message
     * is forwarded and, for each connection-token in this field, remove any
     * header field(s) from the message with the same name as the
     * connection-token.
     * 
     * @param headers
     *            The headers to modify
     */
    private void stripConnectionTokens(HttpHeaders headers) {
        if (headers.contains(HttpHeaderNames.CONNECTION.toString())) {
            for (String headerValue : headers.getAll(HttpHeaderNames.CONNECTION.toString())) {
                for (String connectionToken : ProxyUtils.splitCommaSeparatedHeaderValues(headerValue)) {
                    // do not strip out the Transfer-Encoding header if it is specified in the Connection header, since LittleProxy does not
                    // normally modify the Transfer-Encoding of the message.
                    if (!LOWERCASE_TRANSFER_ENCODING_HEADER.equals(connectionToken.toLowerCase(Locale.US))) {
                        headers.remove(connectionToken);
                    }
                }
            }
        }
    }

    /**
     * Removes all headers that should not be forwarded. See RFC 2616 13.5.1
     * End-to-end and Hop-by-hop Headers.
     * 
     * @param headers
     *            The headers to modify
     */
    private void stripHopByHopHeaders(HttpHeaders headers) {
        Set<String> headerNames = headers.names();
        for (String headerName : headerNames) {
            if (ProxyUtils.shouldRemoveHopByHopHeader(headerName)) {
                headers.remove(headerName);
            }
        }
    }

    /***************************************************************************
     * Miscellaneous
     **************************************************************************/

    /**
     * Tells the client that something went wrong trying to proxy its request. If the Bad Gateway is a response to
     * an HTTP HEAD request, the response will contain no body, but the Content-Length header will be set to the
     * value it would have been if this 502 Bad Gateway were in response to a GET.
     *
     * @param httpRequest the HttpRequest that is resulting in the Bad Gateway response
     * @return true if the connection will be kept open, or false if it will be disconnected
     */
    private boolean writeBadGateway(HttpRequestWrapper httpRequest) {
        String body = "Bad Gateway: " + httpRequest.uri();
        FullHttpResponse response = ProxyUtils.createFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_GATEWAY, body);

        if (ProxyUtils.isHEAD(httpRequest)) {
            // don't allow any body content in response to a HEAD request
            response.content().clear();
        }

        return respondWithShortCircuitResponse(response);
    }

    /**
     * Tells the client that the connection to the server, or possibly to some intermediary service (such as DNS), timed out.
     * If the Gateway Timeout is a response to an HTTP HEAD request, the response will contain no body, but the
     * Content-Length header will be set to the value it would have been if this 504 Gateway Timeout were in response to a GET.
     *
     * @param httpRequest the HttpRequest that is resulting in the Gateway Timeout response
     * @return true if the connection will be kept open, or false if it will be disconnected
     */
    private boolean writeGatewayTimeout() {
        String body = "Gateway Timeout";
        FullHttpResponse response = ProxyUtils.createFullHttpResponse(HttpVersion.HTTP_1_1,
                HttpResponseStatus.GATEWAY_TIMEOUT, body);
        return respondWithShortCircuitResponse(response);
    }

    /**
     * Responds to the client with the specified "short-circuit" response. The response will be sent through the
     * {@link HttpFilters#proxyToClientResponse(HttpObject)} filter method before writing it to the client. The client
     * will not be disconnected, unless the response includes a "Connection: close" header, or the filter returns
     * a null HttpResponse (in which case no response will be written to the client and the connection will be
     * disconnected immediately). If the response is not a Bad Gateway or Gateway Timeout response, the response's headers
     * will be modified to reflect proxying, including adding a Via header, Date header, etc.
     *
     * @param httpResponse the response to return to the client
     * @return true if the connection will be kept open, or false if it will be disconnected.
     */
    private boolean respondWithShortCircuitResponse(HttpResponse httpResponse) {
        // we are sending a response to the client, so we are done handling this request
        httpResponse = (HttpResponse) currentFilters.proxyToClientResponse(httpResponse);
        if (httpResponse == null) {
            disconnect();
            return false;
        }

        // allow short-circuit messages to close the connection. normally the Connection header would be stripped when modifying
        // the message for proxying, so save the keep-alive status before the modifications are made.
        boolean isKeepAlive = HttpUtil.isKeepAlive(httpResponse);

        // if the response is not a Bad Gateway or Gateway Timeout, modify the headers "as if" the short-circuit response were proxied
        int statusCode = httpResponse.status().code();
        if (statusCode != HttpResponseStatus.BAD_GATEWAY.code() && statusCode != HttpResponseStatus.GATEWAY_TIMEOUT.code()) {
            modifyResponseHeadersToReflectProxying(httpResponse);
        }

        // restore the keep alive status, if it was overwritten when modifying headers for proxying
        HttpUtil.setKeepAlive(httpResponse, isKeepAlive);

        write(httpResponse);

        if (ProxyUtils.isLastChunk(httpResponse)) {
            writeEmptyBuffer();
        }

        if (!HttpUtil.isKeepAlive(httpResponse)) {
            disconnect();
            return false;
        }

        return true;
    }

    /**
     * Write an empty buffer at the end of a chunked transfer. We need to do
     * this to handle the way Netty creates HttpChunks from responses that
     * aren't in fact chunked from the remote server using Transfer-Encoding:
     * chunked. Netty turns these into pseudo-chunked responses in cases where
     * the response would otherwise fill up too much memory or where the length
     * of the response body is unknown. This is handy because it means we can
     * start streaming response bodies back to the client without reading the
     * entire response. The problem is that in these pseudo-cases the last chunk
     * is encoded to null, and this thwarts normal ChannelFutures from
     * propagating operationComplete events on writes to appropriate channel
     * listeners. We work around this by writing an empty buffer in those cases
     * and using the empty buffer's future instead to handle any operations we
     * need to when responses are fully written back to clients.
     */
    private void writeEmptyBuffer() {
        write(Unpooled.EMPTY_BUFFER);
    }

    /***************************************************************************
     * Activity Tracking/Statistics
     * 
     * We track statistics on bytes, requests and responses by adding handlers
     * at the appropriate parts of the pipeline (see initChannelPipeline()).
     **************************************************************************/
    private RequestReadMonitor requestReadMonitor = new RequestReadMonitor() {
        @Override
        protected void requestRead(HttpRequest httpRequest) {
            FlowContext flowContext = flowContext();
            for (ActivityTracker tracker : proxyServer
                    .getActivityTrackers()) {
                tracker.requestReceivedFromClient(flowContext, httpRequest);
            }
        }
    };

    private ResponseWrittenMonitor responseWrittenMonitor = new ResponseWrittenMonitor() {
        @Override
        protected void responseWritten(HttpResponse httpResponse) {
            FlowContext flowContext = flowContext();
            for (ActivityTracker tracker : proxyServer
                    .getActivityTrackers()) {
                tracker.responseSentToClient(flowContext,
                        httpResponse);
            }
        }
    };

    private void recordClientConnected() {
        try {
            InetSocketAddress clientAddress = getClientAddress();
            for (ActivityTracker tracker : proxyServer
                    .getActivityTrackers()) {
                tracker.clientConnected(clientAddress);
            }
        } catch (Exception e) {
            LOG.error("Unable to recordClientConnected", e);
        }
    }

    private void recordClientSSLHandshakeSucceeded() {
        try {
            InetSocketAddress clientAddress = getClientAddress();
            for (ActivityTracker tracker : proxyServer
                    .getActivityTrackers()) {
                tracker.clientSSLHandshakeSucceeded(
                        clientAddress, clientSslSession);
            }
        } catch (Exception e) {
            LOG.error("Unable to recorClientSSLHandshakeSucceeded", e);
        }
    }

    private void recordClientDisconnected() {
        try {
            InetSocketAddress clientAddress = getClientAddress();
            for (ActivityTracker tracker : proxyServer
                    .getActivityTrackers()) {
                tracker.clientDisconnected(
                        clientAddress, clientSslSession);
            }
        } catch (Exception e) {
            LOG.error("Unable to recordClientDisconnected", e);
        }
    }

    public InetSocketAddress getClientAddress() {
        if (channel == null) {
            return null;
        }
        return (InetSocketAddress) channel.remoteAddress();
    }

    private FlowContext flowContext() {
        if (currentServerConnection != null) {
            return new FullFlowContext(this, currentServerConnection);
        } else {
            return new FlowContext(this);
        }
    } 

    /**
     * Build an {@link InetSocketAddress} for the given hostAndPort.
     *
     * @return a resolved InetSocketAddress for the specified hostAndPort
     * @throws UnknownHostException if hostAndPort could not be resolved, or if the input string could not be parsed into
     *          a host and port.
     */
    private InetSocketAddress addressFor(HttpProxy proxy, DefaultHttpProxyServer proxyServer)
            throws UnknownHostException {
        return proxyServer.getServerResolver().resolve(proxy);
    }

}
