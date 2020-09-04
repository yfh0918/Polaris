package com.polaris.container.gateway;

import java.net.InetSocketAddress;
import java.util.Map;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.container.gateway.pojo.HttpFilterMessage;
import com.polaris.container.gateway.proxy.FullFlowContext;
import com.polaris.container.gateway.proxy.HttpFiltersAdapter;
import com.polaris.container.gateway.proxy.impl.ProxyToServerConnection;
import com.polaris.container.gateway.request.HttpRequestFilterChain;
import com.polaris.container.gateway.response.HttpResponseFilterChain;
import com.polaris.container.gateway.util.RequestUtil;
import com.polaris.core.Constant;
import com.polaris.core.naming.NamingClient;
import com.polaris.core.pojo.Server;
import com.polaris.core.util.ResultUtil;
import com.polaris.core.util.StringUtil;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http2.HttpConversionUtil;
import io.netty.util.CharsetUtil;

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
            ImmutablePair<Boolean, HttpFilterMessage> immutablePair = HttpRequestFilterChain.INSTANCE.doFilter(originalRequest, httpObject);
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
        
        return wrapperHttp2(httpResponse);
    }
    
    @Override
    public void proxyToServerResolutionSucceeded(FullFlowContext flowContext,String serverHostAndPort,
                                                 InetSocketAddress resolvedRemoteAddress) {
        if (resolvedRemoteAddress == null) {
        	if (ctx.channel().isWritable()) {
                ctx.writeAndFlush(wrapperHttp2(createResponse(originalRequest, 
                		HttpFilterMessage.of(
                				ResultUtil.create(
                						Constant.RESULT_FAIL,Constant.MESSAGE_GLOBAL_ERROR).toJSONString(),
                						HttpResponseStatus.BAD_GATEWAY))));
        	}
        } 
    }

    @Override
    public HttpObject proxyToClientResponse(HttpObject httpObject) {
        if (httpObject instanceof HttpResponse) {
        	if (((HttpResponse) httpObject).status().code() == HttpResponseStatus.BAD_GATEWAY.code()) {
        	    httpObject = createResponse(originalRequest, 
                        HttpFilterMessage.of(
                                ResultUtil.create(
                                        Constant.RESULT_FAIL,Constant.MESSAGE_GLOBAL_ERROR).toJSONString(),
                                        HttpResponseStatus.BAD_GATEWAY));
                return wrapperHttp2((HttpResponse)httpObject);
        	}

        	ImmutablePair<Boolean, HttpFilterMessage> immutablePair = HttpResponseFilterChain.INSTANCE.doFilter(originalRequest, (HttpResponse) httpObject);
        	if (immutablePair.left) {
        	    httpObject = createResponse(originalRequest, immutablePair.right);
                return wrapperHttp2((HttpResponse)httpObject);
        	}
        	
        	return wrapperHttp2((HttpResponse)httpObject);
        }
        return httpObject;
    }

    @Override
    public void proxyToServerConnectionFailed(FullFlowContext flowContext) {
    	ProxyToServerConnection proxyToServerConnection = flowContext.getServerConnection();
        String remoteIp = proxyToServerConnection.getRemoteAddress().getAddress().getHostAddress();
        int remotePort = proxyToServerConnection.getRemoteAddress().getPort();
        NamingClient.onConnectionFail(Server.of(remoteIp, remotePort));
    }

	private FullHttpResponse createResponse(HttpRequest originalRequest, HttpFilterMessage message) {
	    FullHttpResponse httpResponse;
        if (StringUtil.isNotEmpty(message.getResult())) {
        	ByteBuf buf = io.netty.buffer.Unpooled.copiedBuffer(message.getResult(), CharsetUtil.UTF_8); 
        	httpResponse  = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, message.getStatus(), buf);
        } else {
            httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, message.getStatus());
        }
        HttpHeaders httpHeaders=new DefaultHttpHeaders();
        if (message.getHeader() != null) {
            for (Map.Entry<String, Object> entry : message.getHeader().entrySet()) {
                httpHeaders.set(entry.getKey(), entry.getValue());
            }
        }
    	httpHeaders.set(HttpHeaderNames.CONTENT_TYPE, "application/json");
    	httpHeaders.set(HttpHeaderNames.CONTENT_LENGTH,String.valueOf(httpResponse.content().readableBytes()));
        httpResponse.headers().add(httpHeaders);
        return httpResponse;
    }
	
	private HttpResponse wrapperHttp2(HttpResponse response) {
	    if (response == null) {
	        return response;
	    }
	    String streamId = originalRequest.headers().get(HttpConversionUtil.ExtensionHeaderNames.STREAM_ID.text());
        if (streamId != null) {
            response.headers().add(HttpConversionUtil.ExtensionHeaderNames.STREAM_ID.text(),streamId);
        }
	    return response;
	}

}
