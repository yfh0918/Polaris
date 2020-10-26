package com.polaris.container.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.container.gateway.pojo.HttpFilterMessage;
import com.polaris.container.gateway.proxy.FullFlowContext;
import com.polaris.container.gateway.proxy.HttpFiltersAdapter;
import com.polaris.container.gateway.proxy.impl.ProxyToServerConnection;
import com.polaris.container.gateway.request.HttpRequestFilterChain;
import com.polaris.container.gateway.response.HttpResponseFilterChain;
import com.polaris.container.gateway.util.RequestUtil;
import com.polaris.container.gateway.util.ResponseUtil;
import com.polaris.core.Constant;
import com.polaris.core.naming.NamingClient;
import com.polaris.core.pojo.Server;
import com.polaris.core.util.ResultUtil;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * @author:Tom.Yu
 *
 * Description:
 *
 */
public class HttpFilterAdapterImpl extends HttpFiltersAdapter {
	private static Logger logger = LoggerFactory.getLogger(HttpFilterAdapterImpl.class);
	
	private HttpResponse originalResponse;

    public HttpFilterAdapterImpl(HttpRequest originalRequest, ChannelHandlerContext ctx) {
        super(originalRequest, ctx);
    }

    @Override
    public HttpResponse clientToProxyRequest(HttpObject httpObject) {
    	
        HttpResponse httpResponse = null;
        try {
        	if (httpObject instanceof HttpRequest) {
        		RequestUtil.clearLocalThread();//clear local thread
        	}
        	HttpFilterMessage httpFilterMessage = HttpRequestFilterChain.INSTANCE.doFilter(this.originalRequest, httpObject);
            if (httpFilterMessage != null) {
                httpResponse = ResponseUtil.createResponse(originalRequest, httpFilterMessage);//blacklist
            }
        } catch (Exception e) {
            httpResponse = ResponseUtil.createResponse(originalRequest, 
            		HttpFilterMessage.of(
            				ResultUtil.create(Constant.RESULT_FAIL,e.toString()).toJSONString(),
            				HttpResponseStatus.BAD_GATEWAY));
            logger.error("client's request failed", e);
        } 
        
        return httpResponse;
    }
    
    @Override
    public HttpObject proxyToClientResponse(HttpObject httpObject) {
        if (httpObject instanceof HttpResponse) {
            originalResponse = (HttpResponse) httpObject;
        	if (((HttpResponse) httpObject).status().code() == HttpResponseStatus.BAD_GATEWAY.code()) {
        	    httpObject = ResponseUtil.createResponse(originalResponse, 
                        HttpFilterMessage.of(
                                ResultUtil.create(
                                        Constant.RESULT_FAIL,Constant.MESSAGE_GLOBAL_ERROR).toJSONString(),
                                        HttpResponseStatus.BAD_GATEWAY));
                return (HttpResponse)httpObject;
        	}
        	
        }
        HttpFilterMessage message = HttpResponseFilterChain.INSTANCE.doFilter(originalResponse, httpObject);
        if (message != null) {
            httpObject = ResponseUtil.createResponse(originalResponse, message);
            return (HttpResponse)httpObject;
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

	
}
