package com.polaris.container.gateway.response;

import com.polaris.container.gateway.HttpFilter;

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
	public void doStart() {
		super.doStart();
		HttpResponseFilterChain.INSTANCE.add(this);
	} 
	
    /**
     * 从调用链去除过滤器
     *
     */
	@Override
	public void doStop() {
		HttpResponseFilterChain.INSTANCE.remove(this);
	}
	
}
