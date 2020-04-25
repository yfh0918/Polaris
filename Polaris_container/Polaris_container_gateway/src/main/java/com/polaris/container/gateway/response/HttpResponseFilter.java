package com.polaris.container.gateway.response;

import com.polaris.container.gateway.HttpFilter;
import com.polaris.container.gateway.pojo.HttpFilterEntity;

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
	public void start(HttpFilterEntity httpFilterEntity) {
		super.start(httpFilterEntity);
		HttpResponseFilterChain.addFilter(httpFilterEntity);
	} 
	
    /**
     * 从调用链去除过滤器
     *
     */
	@Override
	public void stop(HttpFilterEntity httpFilterEntity) {
		super.stop(httpFilterEntity);
		HttpResponseFilterChain.removeFilter(httpFilterEntity);
	}
	
	protected abstract boolean doFilter(HttpRequest originalRequest, HttpResponse httpResponse);
}
