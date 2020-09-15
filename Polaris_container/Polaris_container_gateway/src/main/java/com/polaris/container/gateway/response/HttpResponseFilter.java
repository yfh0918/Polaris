package com.polaris.container.gateway.response;

import com.polaris.container.gateway.HttpFilter;
import com.polaris.core.component.LifeCycle;

import io.netty.handler.codec.http.HttpResponse;

/**
 * @author:Tom.Yu
 *
 * Description:
 *
 */
public abstract class HttpResponseFilter extends HttpFilter<HttpResponse> {
	
    /**
     * 构造函数并加入调用链
     *
     */
	@Override
	public void starting(LifeCycle event) {
		super.starting(event);
		HttpResponseFilterChain.INSTANCE.add(this);
	}
	
    /**
     * 从调用链去除过滤器
     *
     */
	@Override
	public void stopping(LifeCycle event) {
		super.stopping(event);
		HttpResponseFilterChain.INSTANCE.remove(this);
	}
	
}
