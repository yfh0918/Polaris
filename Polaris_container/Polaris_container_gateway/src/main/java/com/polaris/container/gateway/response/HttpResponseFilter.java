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
	public void start() {
		HttpResponseFilterChain.addFilter(this);
	} 
	
    /**
     * 从调用链去除过滤器
     *
     */
	@Override
	public void stop() {
		HttpResponseFilterChain.removeFilter(this);
	}
	
	protected abstract boolean doFilter(HttpRequest originalRequest, HttpResponse httpResponse);
}
