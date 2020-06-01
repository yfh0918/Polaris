package com.polaris.container.gateway.response;

import com.polaris.container.gateway.HttpFilter;
import com.polaris.core.component.LifeCycle;

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
	public void lifeCycleStarting(LifeCycle event) {
		super.lifeCycleStarting(event);
		HttpResponseFilterChain.INSTANCE.add(this);
	}
	
    /**
     * 从调用链去除过滤器
     *
     */
	@Override
	public void lifeCycleStopping(LifeCycle event) {
		super.lifeCycleStopping(event);
		HttpResponseFilterChain.INSTANCE.remove(this);
	}
	
}
