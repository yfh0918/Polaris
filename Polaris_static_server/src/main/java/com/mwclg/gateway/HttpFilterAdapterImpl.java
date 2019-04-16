package com.mwclg.gateway;

import org.littleshoot.proxy.HttpFiltersAdapter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

/**
 * @author:Tom.Yu
 *
 * Description:
 *
 */
public class HttpFilterAdapterImpl extends HttpFiltersAdapter {

    //构造过滤器适配器
    public HttpFilterAdapterImpl(HttpRequest originalRequest, ChannelHandlerContext ctx) {
        super(originalRequest, ctx);
    }

    //处理所有的request请求
    @Override
    public HttpResponse clientToProxyRequest(HttpObject httpObject) {
    	
        HttpResponse httpResponse = null;
		//静态页面
		try {
			httpResponse = HttpStatic.showStatic(originalRequest,ctx);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return httpResponse;
        
    }
    
    
    @Override
    public HttpObject proxyToClientResponse(HttpObject httpObject) {
        return httpObject;
    }
}
