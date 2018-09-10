package com.polaris.gateway.response;

import javax.annotation.PostConstruct;

import com.polaris.gateway.HttpFilterOrder;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

/**
 * @author:Winning
 *
 * Description:
 *
 */
public abstract class HttpResponseFilter extends HttpFilterOrder {
	
    /**
     * 构造函数并加入调用链
     *
     */
	@PostConstruct
	protected void addFilterChain() {
		HttpResponseFilterChain.addFilter(this);
	} 
	
	abstract HttpResponse doFilter(HttpRequest originalRequest, HttpResponse httpResponse);
}
