package com.polaris.container.gateway;

import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.util.Map;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.container.gateway.proxy.FullFlowContext;
import com.polaris.container.gateway.proxy.HttpFiltersAdapter;
import com.polaris.container.gateway.proxy.impl.ClientToProxyConnection;
import com.polaris.container.gateway.proxy.impl.ProxyToServerConnection;
import com.polaris.container.gateway.request.HttpRequestFilter;
import com.polaris.container.gateway.request.HttpRequestFilterChain;
import com.polaris.container.gateway.response.HttpResponseFilter;
import com.polaris.container.gateway.response.HttpResponseFilterChain;
import com.polaris.container.gateway.util.RequestUtil;
import com.polaris.core.Constant;
import com.polaris.core.naming.provider.ServerStrategyProviderFactory;
import com.polaris.core.pojo.Server;
import com.polaris.core.util.ResultUtil;
import com.polaris.core.util.StringUtil;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

/**
 * @author:Tom.Yu
 *
 * Description:
 *
 */
public class HttpFilterAdapterImpl extends HttpFiltersAdapter {
	private static Logger logger = LoggerFactory.getLogger(HttpFilterAdapterImpl.class);

    public HttpFilterAdapterImpl(HttpRequest originalRequest, ChannelHandlerContext ctx) {
        super(originalRequest, ctx);
    }

    @Override
    public HttpResponse clientToProxyRequest(HttpObject httpObject) {
    	
        HttpResponse httpResponse = null;
        try {
        	if (httpObject instanceof HttpRequest) {
        		RequestUtil.remove();
        	}
            ImmutablePair<Boolean, HttpRequestFilter> immutablePair = HttpRequestFilterChain.INSTANCE.doFilter(originalRequest, httpObject, ctx);
            if (immutablePair.left) {
                httpResponse = createResponse(originalRequest, immutablePair.right);
            }
        } catch (Exception e) {
            httpResponse = createResponse(originalRequest, 
            		HttpFilterMessage.of(
            				ResultUtil.create(Constant.RESULT_FAIL,e.toString()).toJSONString(),
            				HttpResponseStatus.BAD_GATEWAY));
            logger.error("client's request failed", e);
        } 
        
        return httpResponse;
    }
    
    @Override
    public void proxyToServerResolutionSucceeded(String serverHostAndPort,
                                                 InetSocketAddress resolvedRemoteAddress) {
        if (resolvedRemoteAddress == null) {
        	if (ctx.channel().isWritable()) {
                ctx.writeAndFlush(createResponse(originalRequest, 
                		HttpFilterMessage.of(
                				ResultUtil.create(
                						Constant.RESULT_FAIL,Constant.MESSAGE_GLOBAL_ERROR).toJSONString(),
                						HttpResponseStatus.BAD_GATEWAY)));
        	}
        } 
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
    public void proxyToServerRequestSending(FullFlowContext flowContext, HttpRequest httpRequest) {
    	ClientToProxyConnection clientToProxyConnection = flowContext.getClientConnection();
    	ProxyToServerConnection proxyConnection = flowContext.getServerConnection();
        logger.debug("client channel:{}-{}", clientToProxyConnection.getChannel().localAddress().toString(), clientToProxyConnection.getChannel().remoteAddress().toString());
        logger.debug("server channel:{}-{}", proxyConnection.getChannel().localAddress().toString(), proxyConnection.getChannel().remoteAddress().toString());
        proxyConnection.getChannel().closeFuture().addListener(new GenericFutureListener() {
            @Override
            public void operationComplete(Future future) {
                if (clientToProxyConnection.getChannel().isActive()) {
                    logger.debug("channel:{}-{} will be closed", clientToProxyConnection.getChannel().localAddress().toString(), clientToProxyConnection.getChannel().remoteAddress().toString());
                    clientToProxyConnection.getChannel().close();
                } else {
                    logger.debug("channel:{}-{} has been closed", clientToProxyConnection.getChannel().localAddress().toString(), clientToProxyConnection.getChannel().remoteAddress().toString());
                }
            }
        });
    }
    
    @Override
    public HttpObject proxyToClientResponse(HttpObject httpObject) {
        if (httpObject instanceof HttpResponse) {
        	
        	if (((HttpResponse) httpObject).status().code() == HttpResponseStatus.BAD_GATEWAY.code()) {
                ctx.writeAndFlush(createResponse(originalRequest, 
                		HttpFilterMessage.of(
                				ResultUtil.create(
                						Constant.RESULT_FAIL,Constant.MESSAGE_GLOBAL_ERROR).toJSONString(),
                						HttpResponseStatus.BAD_GATEWAY)));
                return httpObject;
        	}

        	ImmutablePair<Boolean, HttpResponseFilter> immutablePair = HttpResponseFilterChain.INSTANCE.doFilter(originalRequest, (HttpResponse) httpObject);
        	
        	if (immutablePair.left) {
        		ctx.writeAndFlush(createResponse(originalRequest, immutablePair.right));
                return httpObject;
        	}
        }
        return httpObject;
    }

    @Override
    public void proxyToServerConnectionSucceeded(final ChannelHandlerContext serverCtx) {
        ChannelPipeline pipeline = serverCtx.pipeline();
        if (pipeline.get("inflater") != null) {
            pipeline.remove("inflater");
        }
        if (pipeline.get("aggregator") != null) {
            pipeline.remove("aggregator");
        }
        super.proxyToServerConnectionSucceeded(serverCtx);    
    }
    
    @Override
    public void proxyToServerConnectionFailed() {
        try {
   		 	ClientToProxyConnection clientToProxyConnection = (ClientToProxyConnection) ctx.handler();
            Field field = ClientToProxyConnection.class.getDeclaredField("currentServerConnection");
            field.setAccessible(true);
            ProxyToServerConnection proxyToServerConnection = (ProxyToServerConnection) field.get(clientToProxyConnection);
            String remoteIp = proxyToServerConnection.getRemoteAddress().getAddress().getHostAddress();
            int remotePort = proxyToServerConnection.getRemoteAddress().getPort();
            ServerStrategyProviderFactory.get().onConnectionFail(Server.of(remoteIp, remotePort));
        } catch (Exception e) {
            logger.error("connection of proxy->server is failed", e);
        } 
    }

	private HttpResponse createResponse(HttpRequest originalRequest, HttpFilterMessage message) {
        HttpResponse httpResponse;
        if (StringUtil.isNotEmpty(message.getResult())) {
        	ByteBuf buf = io.netty.buffer.Unpooled.copiedBuffer(message.getResult(), CharsetUtil.UTF_8); 
        	httpResponse  = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, message.getStatus(), buf);
        } else {
            httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, message.getStatus());
        }
        HttpHeaders httpHeaders=new DefaultHttpHeaders();
        httpHeaders.add("Transfer-Encoding","chunked");
    	httpHeaders.set("Content-Type", "application/json");
    	if (message.getHeader() != null) {
    		for (Map.Entry<String, Object> entry : message.getHeader().entrySet()) {
    			httpHeaders.set(entry.getKey(), entry.getValue());
    		}
    	}
        httpResponse.headers().add(httpHeaders);
        return httpResponse;
    }
}
