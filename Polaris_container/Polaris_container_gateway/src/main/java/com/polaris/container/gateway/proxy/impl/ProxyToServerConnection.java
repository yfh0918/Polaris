package com.polaris.container.gateway.proxy.impl;

import static com.polaris.container.gateway.proxy.impl.ConnectionState.AWAITING_CHUNK;
import static com.polaris.container.gateway.proxy.impl.ConnectionState.AWAITING_INITIAL;
import static com.polaris.container.gateway.proxy.impl.ConnectionState.CONNECTING;
import static com.polaris.container.gateway.proxy.impl.ConnectionState.DISCONNECTED;
import static com.polaris.container.gateway.proxy.impl.ConnectionState.HANDSHAKING;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.RejectedExecutionException;

import com.barchart.udt.nio.SelectorProviderUDT;
import com.polaris.container.gateway.pojo.HttpRequestWrapper;
import com.polaris.container.gateway.proxy.ActivityTracker;
import com.polaris.container.gateway.proxy.FullFlowContext;
import com.polaris.container.gateway.proxy.HttpFilters;
import com.polaris.container.gateway.proxy.TransportProtocol;
import com.polaris.container.gateway.proxy.UnknownTransportProtocolException;
import com.polaris.container.gateway.util.ProxyUtils;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.handler.traffic.GlobalTrafficShapingHandler;
import io.netty.util.ReferenceCounted;
import io.netty.util.concurrent.Future;

/**
 * <p>
 * Represents a connection from our proxy to a server on the web.
 * ProxyConnections are reused fairly liberally, and can go from disconnected to
 * connected, back to disconnected and so on.
 * </p>
 *
 * <p>
 * Connecting a {@link ProxyToServerConnection} can involve more than just
 * connecting the underlying {@link Channel}. In particular, the connection may
 * use encryption (i.e. TLS) and it may also establish an HTTP CONNECT tunnel.
 * The various steps involved in fully establishing a connection are
 * encapsulated in the property {@link #connectionFlow}, which is initialized in
 * {@link #initializeConnectionFlow()}.
 * </p>
 */
@Sharable
public class ProxyToServerConnection extends ProxyConnection<HttpResponse> {
    private final ClientToProxyConnection clientConnection;
    private volatile TransportProtocol transportProtocol;
    private volatile InetSocketAddress remoteAddress;
    private volatile InetSocketAddress localAddress;

    /**
     * The filters to apply to response/chunks received from server.
     */
    private volatile HttpFilters currentFilters;

    /**
     * Encapsulates the flow for establishing a connection, which can vary
     * depending on how things are configured.
     */
    private volatile ConnectionFlow connectionFlow;

    /**
     * While we're in the process of connecting, it's possible that we'll
     * receive a new message to write. This lock helps us synchronize and wait
     * for the connection to be established before writing the next message.
     */
    private final Object connectLock = new Object();

    /**
     * Keeps track of HttpRequests that have been issued so that we can
     * associate them with responses that we get back
     */
    private volatile HttpRequestWrapper currentHttpRequest;

    /**
     * While we're doing a chunked transfer, this keeps track of the initial
     * HttpResponse object for our transfer (which is useful for its headers).
     */
    private volatile HttpResponse currentHttpResponse;

    /**
     * Limits bandwidth when throttling is enabled.
     */
    private volatile GlobalTrafficShapingHandler trafficHandler;
    
    /**
     * flowContext
     */
    private volatile FullFlowContext flowContext;
    
    /**
     * Create a new ProxyToServerConnection.
     *
     * @param proxyServer
     * @param clientConnection
     * @param serverHostAndPort
     * @param initialFilters
     * @param initialHttpRequest
     * @return
     * @throws UnknownHostException
     */
    static ProxyToServerConnection create(DefaultHttpProxyServer proxyServer,
                                          ClientToProxyConnection clientConnection,
                                          HttpFilters initialFilters,
                                          InetSocketAddress remoteAddress,
                                          GlobalTrafficShapingHandler globalTrafficShapingHandler)
            throws UnknownHostException {
        return new ProxyToServerConnection(proxyServer,
                clientConnection,
                initialFilters,
                globalTrafficShapingHandler,
                remoteAddress
                );
    }

    private ProxyToServerConnection(
            DefaultHttpProxyServer proxyServer,
            ClientToProxyConnection clientConnection,
            HttpFilters initialFilters,
            GlobalTrafficShapingHandler globalTrafficShapingHandler,
            InetSocketAddress remoteAddress
            )
            throws UnknownHostException {
        super(DISCONNECTED, proxyServer);
        this.clientConnection = clientConnection;
        this.trafficHandler = globalTrafficShapingHandler;
        this.currentFilters = initialFilters;
        this.remoteAddress = remoteAddress;
        this.flowContext = new FullFlowContext(clientConnection,this);
        // Report connection status to HttpFilters
        currentFilters.proxyToServerConnectionQueued(flowContext);
        setupConnectionParameters();
    }

    /***************************************************************************
     * Reading
     **************************************************************************/

    @Override
    protected void read(Object msg) {
        if (isConnecting()) {
            LOG.debug(
                    "In the middle of connecting, forwarding message to connection flow: {}",
                    msg);
            this.connectionFlow.read(msg);
        } else {
            super.read(msg);
        }
    }

	@Override
    protected ConnectionState readHTTPInitial(HttpResponse httpResponse) {
        LOG.debug("Received raw response: {}", httpResponse);

        if (httpResponse.decoderResult().isFailure()) {
            LOG.debug("Could not parse response from server. Decoder result: {}", httpResponse.decoderResult().toString());

            // create a "substitute" Bad Gateway response from the server, since we couldn't understand what the actual
            // response from the server was. set the keep-alive on the substitute response to false so the proxy closes
            // the connection to the server, since we don't know what state the server thinks the connection is in.
            FullHttpResponse substituteResponse = ProxyUtils.createFullHttpResponse(HttpVersion.HTTP_1_1,
                    HttpResponseStatus.BAD_GATEWAY,
                    "Unable to parse response from server");
            HttpUtil.setKeepAlive(substituteResponse, false);
            httpResponse = substituteResponse;
        }

        currentFilters.serverToProxyResponseReceiving(flowContext);

        rememberCurrentResponse(httpResponse);
        respondWith(httpResponse);

        if (ProxyUtils.isChunked(httpResponse)) {
            return AWAITING_CHUNK;
        } else {
            currentFilters.serverToProxyResponseReceived(flowContext);
            return AWAITING_INITIAL;
        }
    }

    @Override
    protected void readHTTPChunk(HttpContent chunk) {
        respondWith(chunk);
    }

    /**
     * <p>
     * Responses to HEAD requests aren't supposed to have content, but Netty
     * doesn't know that any given response is to a HEAD request, so it needs to
     * be told that there's no content so that it doesn't hang waiting for it.
     * </p>
     *
     * <p>
     * See the documentation for {@link HttpResponseDecoder} for information
     * about why HEAD requests need special handling.
     * </p>
     *
     * <p>
     * Thanks to <a href="https://github.com/nataliakoval">nataliakoval</a> for
     * pointing out that with connections being reused as they are, this needs
     * to be sensitive to the current request.
     * </p>
     */
    private class HeadAwareHttpResponseDecoder extends HttpResponseDecoder {

        public HeadAwareHttpResponseDecoder(int maxInitialLineLength,
                                            int maxHeaderSize, int maxChunkSize) {
            super(maxInitialLineLength, maxHeaderSize, maxChunkSize);
        }

        @Override
        protected boolean isContentAlwaysEmpty(HttpMessage httpMessage) {
            // The current HTTP Request can be null when this proxy is
            // negotiating a CONNECT request with a chained proxy
            // while it is running as a MITM. Since the response to a
            // CONNECT request does not have any content, we return true.
            if(currentHttpRequest == null) {
                return true;
            } else {
                return ProxyUtils.isHEAD(currentHttpRequest) || super.isContentAlwaysEmpty(httpMessage);
            }
        }
    };

    /***************************************************************************
     * Writing
     **************************************************************************/

    /**
     * Like {@link #write(Object)} and also sets the current filters to the
     * given value.
     *
     * @param msg
     * @param filters
     */
    void write(HttpRequestWrapper httpRequest, HttpFilters filters) {
        this.currentFilters = filters;
        this.currentHttpRequest = httpRequest;
        write(httpRequest);
    }

    @Override
    void write(Object msg) {
        LOG.debug("Requested write of {}", msg);

        if (msg instanceof HttpRequestWrapper) {
            if (((HttpRequestWrapper)msg).getOrgHttpRequest() instanceof ReferenceCounted) {
                LOG.debug("Retaining reference counted message");
                ((ReferenceCounted) ((HttpRequestWrapper)msg).getOrgHttpRequest()).retain();
            }
        } else {
            if (msg instanceof ReferenceCounted) {
                LOG.debug("Retaining reference counted message");
                ((ReferenceCounted) msg).retain();
            }
        }

        if (is(DISCONNECTED) && msg instanceof HttpRequestWrapper) {
            LOG.debug("Currently disconnected, connect and then write the message");
            connectAndWrite((HttpRequestWrapper) msg);
        } else {
            if (isConnecting()) {
                synchronized (connectLock) {
                    if (isConnecting()) {
                        LOG.debug("Attempted to write while still in the process of connecting, waiting for connection.");
                        clientConnection.stopReading();
                        try {
                            connectLock.wait(30000);
                        } catch (InterruptedException ie) {
                            LOG.warn("Interrupted while waiting for connect monitor");
                        }
                    }
                }
            }

            // only write this message if a connection was established and is not in the process of disconnecting or
            // already disconnected
            if (isConnecting() || getCurrentState().isDisconnectingOrDisconnected()) {
                LOG.debug("Connection failed or timed out while waiting to write message to server. Message will be discarded: {}", msg);
                return;
            }

            LOG.debug("Using existing connection to: {}", remoteAddress);
            doWrite(msg);
        }
    };

    @Override
    protected void writeHttp(HttpObject httpObject) {
        if (httpObject instanceof HttpRequestWrapper) {
            HttpRequestWrapper httpRequest = (HttpRequestWrapper) httpObject;
            
            // Remember that we issued this HttpRequest for later
            currentHttpRequest = httpRequest;
            
            //replace proxy host for remote connecting
            replaceProxyHostForRemoteConnenct(httpRequest, hostToString(this.remoteAddress));
        }
        super.writeHttp(httpObject);
    }
    
    /**
    * replace http request host for remote connection
    * @return
    */
    private void replaceProxyHostForRemoteConnenct(HttpRequest httpRequest, String host) {
        httpRequest.headers().remove(HttpHeaderNames.HOST.toString());
        httpRequest.headers().add(HttpHeaderNames.HOST.toString(), host);
    }
    private String hostToString(InetSocketAddress address) {
        String host = address.getHostName();
        int port = address.getPort();
        if (port != 80) {
            host = host + ":" + port;
        }
        return host;
    }
    
    /***************************************************************************
     * Lifecycle
     **************************************************************************/

    @Override
    protected void become(ConnectionState newState) {
        // Report connection status to HttpFilters
        if (getCurrentState() == DISCONNECTED && newState == CONNECTING) {
            currentFilters.proxyToServerConnectionStarted(flowContext);
        } else if (getCurrentState() == CONNECTING) {
            if (newState == HANDSHAKING) {
                currentFilters.proxyToServerConnectionSSLHandshakeStarted(flowContext);
            } else if (newState == AWAITING_INITIAL) {
                currentFilters.proxyToServerConnectionSucceeded(flowContext);
            } else if (newState == DISCONNECTED) {
                currentFilters.proxyToServerConnectionFailed(flowContext);
            }
        } else if (getCurrentState() == HANDSHAKING) {
            if (newState == AWAITING_INITIAL) {
                currentFilters.proxyToServerConnectionSucceeded(flowContext);
            } else if (newState == DISCONNECTED) {
                currentFilters.proxyToServerConnectionFailed(flowContext);
            }
        } else if (getCurrentState() == AWAITING_CHUNK
                && newState != AWAITING_CHUNK) {
            currentFilters.serverToProxyResponseReceived(flowContext);
        }

        super.become(newState);
    }

    @Override
    protected void becameSaturated() {
        super.becameSaturated();
        this.clientConnection.serverBecameSaturated(this);
    }

    @Override
    protected void becameWritable() {
        super.becameWritable();
        this.clientConnection.serverBecameWriteable(this);
    }

    @Override
    protected void timedOut() {
        super.timedOut();
        clientConnection.timedOut(this);
    }

    @Override
    protected void disconnected() {
        super.disconnected();
        clientConnection.serverDisconnected(this);
    }

    @Override
    protected void exceptionCaught(Throwable cause) {
        try {
            if (cause instanceof IOException) {
                // IOExceptions are expected errors, for example when a server drops the connection. rather than flood
                // the logs with stack traces for these expected exceptions, log the message at the INFO level and the
                // stack trace at the DEBUG level.
                LOG.info("An IOException occurred on ProxyToServerConnection: " + cause.getMessage());
                LOG.debug("An IOException occurred on ProxyToServerConnection", cause);
            } else if (cause instanceof RejectedExecutionException) {
                LOG.info("An executor rejected a read or write operation on the ProxyToServerConnection (this is normal if the proxy is shutting down). Message: " + cause.getMessage());
                LOG.debug("A RejectedExecutionException occurred on ProxyToServerConnection", cause);
            } else {
                LOG.error("Caught an exception on ProxyToServerConnection", cause);
            }
        } finally {
            if (!is(DISCONNECTED)) {
                LOG.info("Disconnecting open connection to server");
                disconnect();
            }
        }
        // This can happen if we couldn't make the initial connection due
        // to something like an unresolved address, for example, or a timeout.
        // There will not have been be any requests written on an unopened
        // connection, so there should not be any further action to take here.
    }

    /***************************************************************************
     * State Management
     **************************************************************************/
    public TransportProtocol getTransportProtocol() {
        return transportProtocol;
    }

    public InetSocketAddress getRemoteAddress() {
        return remoteAddress;
    }


    @Override
    protected HttpFilters getHttpFiltersFromProxyServer(HttpRequest httpRequest) {
        return currentFilters;
    }

    /***************************************************************************
     * Private Implementation
     **************************************************************************/

    /**
     * Keeps track of the current HttpResponse so that we can associate its
     * headers with future related chunks for this same transfer.
     *
     * @param response
     */
    private void rememberCurrentResponse(HttpResponse response) {
        LOG.debug("Remembering the current response.");
        // We need to make a copy here because the response will be
        // modified in various ways before we need to do things like
        // analyze response headers for whether or not to close the
        // connection (which may not happen for a while for large, chunked
        // responses, for example).
        currentHttpResponse = ProxyUtils.copyMutableResponseFields(response);
    }

    /**
     * Respond to the client with the given {@link HttpObject}.
     *
     * @param httpObject
     */
    private void respondWith(HttpObject httpObject) {
        clientConnection.respond(this, currentFilters, currentHttpRequest,
                currentHttpResponse, httpObject);
    }

    /**
     * Configures the connection to the upstream server and begins the {@link ConnectionFlow}.
     *
     * @param initialRequest the current HTTP request being handled
     */
    private void connectAndWrite(HttpRequestWrapper currentHttpRequest) {
        LOG.debug("Starting new connection to: {}", remoteAddress);

        // Remember our initial request so that we can write it after connecting
        this.currentHttpRequest = currentHttpRequest;
        initializeConnectionFlow();
        connectionFlow.start();
    }

    /**
     * This method initializes our {@link ConnectionFlow} based on however this connection has been configured. If
     * the {@link #disableSni} value is true, this method will not pass peer information to the MitmManager when
     * handling CONNECTs.
     */
    private void initializeConnectionFlow() {
        this.connectionFlow = new ConnectionFlow(clientConnection, this,
                connectLock)
                .then(ConnectChannel);
    }

    /**
     * Opens the socket connection.
     */
    private ConnectionFlowStep ConnectChannel = new ConnectionFlowStep(this,
            CONNECTING) {
        @Override
        boolean shouldExecuteOnEventLoop() {
            return false;
        }

		@Override
        protected Future<?> execute() {
		    return connect();
		}
    };
    private ChannelFuture connect() {
        Bootstrap cb = new Bootstrap().group(proxyServer.getProxyToServerWorkerFor(transportProtocol));
        switch (transportProtocol) {
            case TCP:
                LOG.debug("Connecting to server with TCP");
                cb.channelFactory(new io.netty.channel.ChannelFactory<Channel>() {
                    @Override
                    public Channel newChannel() {
                        return new NioSocketChannel();
                    }
                });
                break;
            case UDT:
                LOG.debug("Connecting to server with UDT");
                cb.channelFactory(new io.netty.channel.ChannelFactory<Channel>() {
                    @Override
                    public Channel newChannel() {
                        return new NioSocketChannel(SelectorProviderUDT.STREAM);
                    }
                })
                .option(ChannelOption.SO_REUSEADDR, true);
                
                break;
            
            default:
                throw new UnknownTransportProtocolException(transportProtocol);
        }

        cb.handler(new ChannelInitializer<Channel>() {
            protected void initChannel(Channel ch) throws Exception {
                initChannelPipeline(ch.pipeline());
            };
        });
        cb.option(ChannelOption.CONNECT_TIMEOUT_MILLIS,
                proxyServer.getConnectTimeout());

        if (localAddress != null) {
            return cb.connect(remoteAddress, localAddress);
        } else {
            return cb.connect(remoteAddress);
        }
    
    }

    /**
     * Called when the connection to the server or upstream chained proxy fails. This method may return true to indicate
     * that the connection should be retried. If returning true, this method must set up the connection itself.
     *
     * @param cause the reason that our attempt to connect failed (can be null)
     * @return true if we are trying to fall back to another connection
     */
    protected boolean connectionFailed(Throwable cause)
            throws UnknownHostException {
        // the connection issue wasn't due to an unrecognized_name error, or the connection attempt failed even after
        LOG.info("Connection to upstream server failed", cause);

        // no chained proxy fallback or other retry mechanism available
        return false;
    }


    /**
     * Set up our connection parameters based on server address and chained
     * proxies.
     *
     * @throws UnknownHostException when unable to resolve the hostname to an IP address
     */
    private void setupConnectionParameters() throws UnknownHostException {
        this.transportProtocol = TransportProtocol.TCP;
        this.localAddress = proxyServer.getLocalAddress();
    }

    /**
     * Initialize our {@link ChannelPipeline} to connect the upstream server.
     * LittleProxy acts as a client here.
     *
     * A {@link ChannelPipeline} invokes the read (Inbound) handlers in
     * ascending ordering of the list and then the write (Outbound) handlers in
     * descending ordering.
     *
     * Regarding the Javadoc of {@link HttpObjectAggregator} it's needed to have
     * the {@link HttpResponseEncoder} or {@link HttpRequestEncoder} before the
     * {@link HttpObjectAggregator} in the {@link ChannelPipeline}.
     *
     * @param pipeline
     */
    private void initChannelPipeline(ChannelPipeline pipeline) {

        if (trafficHandler != null) {
            pipeline.addLast("global-traffic-shaping", trafficHandler);
        }
        pipeline.addLast("encoder", new HttpRequestEncoder());
        pipeline.addLast("decoder", new HeadAwareHttpResponseDecoder(
                proxyServer.getMaxInitialLineLength(),
                proxyServer.getMaxHeaderSize(),
                proxyServer.getMaxChunkSize()));
        pipeline.addLast("responseReadMonitor", responseReadMonitor);
        pipeline.addLast("requestWrittenMonitor", requestWrittenMonitor);
        pipeline.addLast(
                "idle",
                new IdleStateHandler(0, 0, proxyServer
                        .getIdleConnectionTimeout()));
        pipeline.addLast("chunkedWriteHandler", new ChunkedWriteHandler());
        pipeline.addLast("handler", this);
    }

    /**
     * <p>
     * Do all the stuff that needs to be done after our {@link ConnectionFlow}
     * has succeeded.
     * </p>
     *
     * @param shouldForwardInitialRequest
     *            whether or not we should forward the initial HttpRequest to
     *            the server after the connection has been established.
     */
    void connectionSucceeded(boolean shouldForwardInitialRequest) {
        become(AWAITING_INITIAL);
        clientConnection.serverConnectionSucceeded(this,
                shouldForwardInitialRequest);

        if (shouldForwardInitialRequest) {
            LOG.debug("Writing initial request: {}", currentHttpRequest);
            write(currentHttpRequest);
        } else {
            LOG.debug("Dropping initial request: {}", currentHttpRequest);
        }

        // we're now done with the initialRequest: it's either been forwarded to the upstream server (HTTP requests), or
        // completely dropped (HTTPS CONNECTs). if the initialRequest is reference counted (typically because the HttpObjectAggregator is in
        // the pipeline to generate FullHttpRequests), we need to manually release it to avoid a memory leak.
        if (currentHttpRequest.getOrgHttpRequest() instanceof ReferenceCounted) {
            ((ReferenceCounted)(currentHttpRequest.getOrgHttpRequest())).release();
        }
    }

    /***************************************************************************
     * Activity Tracking/Statistics
     *
     * We track statistics on bytes, requests and responses by adding handlers
     * at the appropriate parts of the pipeline (see initChannelPipeline()).
     **************************************************************************/
    private ResponseReadMonitor responseReadMonitor = new ResponseReadMonitor() {
        @Override
        protected void responseRead(HttpResponse httpResponse) {
            for (ActivityTracker tracker : proxyServer
                    .getActivityTrackers()) {
                tracker.responseReceivedFromServer(flowContext, httpResponse);
            }
        }
    };

    private RequestWrittenMonitor requestWrittenMonitor = new RequestWrittenMonitor() {
        @Override
        protected void requestWriting(HttpRequest httpRequest) {
            try {
                for (ActivityTracker tracker : proxyServer
                        .getActivityTrackers()) {
                    tracker.requestSentToServer(flowContext, httpRequest);
                }
            } catch (Throwable t) {
                LOG.warn("Error while invoking ActivityTracker on request", t);
            }

            currentFilters.proxyToServerRequestSending(flowContext, httpRequest);
        }

        @Override
        protected void requestWritten(HttpRequest httpRequest) {
        }

        @Override
        protected void contentWritten(HttpContent httpContent) {
            if (httpContent instanceof LastHttpContent) {
                currentFilters.proxyToServerRequestSent(flowContext, (LastHttpContent)httpContent);
            }
        }
    };

    public HttpRequestWrapper getCurrentHttpRequest() {
        return currentHttpRequest;
    }

    public void setCurrentHttpRequest(HttpRequestWrapper currentHttpRequest) {
        this.currentHttpRequest = currentHttpRequest;
    }
}