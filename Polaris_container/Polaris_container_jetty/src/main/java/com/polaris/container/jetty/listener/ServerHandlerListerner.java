package com.polaris.container.jetty.listener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jetty.util.component.LifeCycle;

/**
 * Class Name : ServerHandler
 * Description : 服务器Handler
 * Creator : yufenghua
 * Modifier : yufenghua
 *
 */

public class ServerHandlerListerner implements LifeCycle.Listener {
	
	/**
	 * 服务器监听器集合
	 */
	private Set<ServerListener> serverListeners = null;

	private ServerHandlerListerner() {
		this.serverListeners = new HashSet<ServerListener>();
	}

	/**
	 * 服务器状态变更响应
	 * @param serverStatus 服务器状态
	 */
	public void onServerStatusChanged(int serverStatus) {
		List<ServerListener> sls = new ArrayList<ServerListener>();
		sls.addAll(this.serverListeners);
		for (ServerListener sl : sls) {
			sl.onServerStatusChanged(serverStatus);
		}
	}

	/**
	 * 添加服务器监听器
	 * @param listener 服务器监听器
	 */
	public void addListener(ServerListener listener) {
		this.serverListeners.add(listener);
	}

	/**
	 * 移除服务器监听器
	 * @param listener 服务器监听器
	 */
	public void rmvListener(ServerListener listener) {
		this.serverListeners.remove(listener);
	}

	/**
	 * 获取单实例公共静态方法
	 * @return 单实例
	 */
	public static ServerHandlerListerner getInstance() {
		return Singletone.INSTANCE;
	}

	/**
	 * 静态内部类实现单例
	 *
	 */
	private static class Singletone {
		/**
		 * 单实例
		 */
		private static final ServerHandlerListerner INSTANCE = new ServerHandlerListerner();
	}
	
	/**
	 * 监听server的状态
	 * 启动中
	 */
	public void lifeCycleStarting(LifeCycle event) {
		//启动中不需要任何信息
	}
	
	/**
	 * 监听server的状态
	 * 启动结束
	 */
    public void lifeCycleStarted(LifeCycle event) {

    	// 通知服务器状态改变
		this.onServerStatusChanged(ServerListener.SERVER_STATUS_STARTED);

    }
    
	/**
	 * 监听server的状态
	 * 异常
	 */
    public void lifeCycleFailure(LifeCycle event,Throwable cause) {
		// 通知服务器状态改变
		this.onServerStatusChanged(ServerListener.SERVER_STATUS_ERROR);
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
    	
		// 通知服务器状态改变
		this.onServerStatusChanged(ServerListener.SERVER_STATUS_STOPPED);
    }
}
