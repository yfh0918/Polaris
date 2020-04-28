package com.polaris.container.jetty.listener;

import org.eclipse.jetty.util.component.AbstractLifeCycle.AbstractLifeCycleListener;
import org.eclipse.jetty.util.component.LifeCycle;

import com.polaris.container.listener.ServerListenerHelper;

/**
 * Class Name : ServerHandler
 * Description : 服务器Handler
 * Creator : yufenghua
 * Modifier : yufenghua
 *
 */

public class ServerHandlerListerner extends AbstractLifeCycleListener{

	/**
	 * 服务器监听器集合
	 */

	public ServerHandlerListerner() {
	}

	/**
	 * 监听server的状态
	 * 启动中
	 */
	public void lifeCycleStarting(LifeCycle event) {
		ServerListenerHelper.starting();
	}
	
	/**
	 * 监听server的状态
	 * 启动结束
	 */
    public void lifeCycleStarted(LifeCycle event) {
		ServerListenerHelper.started();
    }
    
	/**
	 * 监听server的状态
	 * 异常
	 */
    public void lifeCycleFailure(LifeCycle event,Throwable cause) {
		ServerListenerHelper.failure();
    }
    
	/**
	 * 监听server的状态
	 * 结束中
	 */
   public void lifeCycleStopping(LifeCycle event) {
       ServerListenerHelper.stopping();
   }
   
	/**
	 * 监听server的状态
	 * 结束
	 */
    public void lifeCycleStopped(LifeCycle event) {
		ServerListenerHelper.stopped();
    }
    
}
