package com.polaris.container.jetty.listener;

import org.eclipse.jetty.util.component.AbstractLifeCycle.AbstractLifeCycleListener;
import org.eclipse.jetty.util.component.LifeCycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.http.supports.ServerListener;

/**
 * Class Name : ServerHandler
 * Description : 服务器Handler
 * Creator : yufenghua
 * Modifier : yufenghua
 *
 */

public class ServerHandlerListerner extends AbstractLifeCycleListener{
	
	private static final Logger logger = LoggerFactory.getLogger(ServerHandlerListerner.class);
	/**
	 * 服务器监听器集合
	 */
	private static ServerHandlerListerner instance = null;
	private ServerListener listener;

	private ServerHandlerListerner(ServerListener listener) {
		this.listener = listener;
	}

	/**
	 * 获取单实例公共静态方法
	 * @return 单实例
	 */
	public static ServerHandlerListerner getInstance(ServerListener listener) {
		if (instance == null) {
			synchronized(ServerHandlerListerner.class) {
				if (instance == null) {
					instance = new ServerHandlerListerner(listener);
				}
			}
		}
		return instance;
	}
	
	/**
	 * 监听server的状态
	 * 启动中
	 */
	public void lifeCycleStarting(LifeCycle event) {
	}
	
	/**
	 * 监听server的状态
	 * 启动结束
	 */
    public void lifeCycleStarted(LifeCycle event) {
    	logger.info("JettyServer启动成功！");
    	listener.started();
    }
    
	/**
	 * 监听server的状态
	 * 异常
	 */
    public void lifeCycleFailure(LifeCycle event,Throwable cause) {
    	logger.info("JettyServer启动失败！");
    }
    
	/**
	 * 监听server的状态
	 * 结束中
	 */
   public void lifeCycleStopping(LifeCycle event) {
	   //结束中不需要任何信息
   }
   
	/**
	 * 监听server的状态
	 * 结束
	 */
    public void lifeCycleStopped(LifeCycle event) {
    	logger.info("JettyServer已经停止！");
    }
    
}
