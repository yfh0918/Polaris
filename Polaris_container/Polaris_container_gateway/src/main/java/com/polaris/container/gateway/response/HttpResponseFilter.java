package com.polaris.container.gateway.response;

import javax.annotation.PostConstruct;

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
	@PostConstruct
	protected void addFilterChain() {
		HttpResponseFilterChain.addFilter(this);
	} 
	
	abstract boolean doFilter(HttpRequest originalRequest, HttpResponse httpResponse);
}
