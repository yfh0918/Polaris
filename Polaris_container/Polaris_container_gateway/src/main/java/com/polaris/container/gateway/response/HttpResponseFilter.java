package com.polaris.container.gateway.response;

import com.polaris.container.gateway.HttpFilter;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

/**
 * @author:Tom.Yu
 *
 * Description:
 *
 */
public abstract class HttpResponseFilter extends HttpFilter {
	
    /**
     * 构造函数并加入调用链
     *
     */
	@Override
	public void init() {
		HttpResponseFilterChain.addFilter(this);
	} 
	
	protected abstract boolean doFilter(HttpRequest originalRequest, HttpResponse httpResponse);
}
