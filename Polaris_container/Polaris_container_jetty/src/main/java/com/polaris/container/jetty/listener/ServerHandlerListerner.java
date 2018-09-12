package com.polaris.container.jetty.listener;

import java.util.HashSet;
import java.util.ServiceLoader;
import java.util.Set;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;

import org.eclipse.jetty.util.component.LifeCycle;

import com.polaris.comm.util.LogUtil;

/**
 * Class Name : ServerHandler
 * Description : 服务器Handler
 * Creator : yufenghua
 * Modifier : yufenghua
 *
 */

public class ServerHandlerListerner implements LifeCycle.Listener {
	
	private static final LogUtil logger = LogUtil.getInstance(ServerHandlerListerner.class);
	/**
	 * 服务器监听器集合
	 */
	private Set<ServerListener> serverListeners = null;
	private final ServletContext sc;
	private static ServerHandlerListerner instance = null;
	private final ServiceLoader<ServletContainerInitializer> serviceLoader = ServiceLoader.load(ServletContainerInitializer.class);

	private ServerHandlerListerner(ServletContext sc) {
		this.sc = sc;
		this.serverListeners = new HashSet<ServerListener>();
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
	public static ServerHandlerListerner getInstance(ServletContext sc) {
		if (instance == null) {
			synchronized(ServerHandlerListerner.class) {
				if (instance == null) {
					instance = new ServerHandlerListerner(sc);
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
		for (ServletContainerInitializer servletContainerInitializer : serviceLoader) {
			try {
				servletContainerInitializer.onStartup(null, this.sc);
			} catch (Exception e) {
				//nothing
			}
		}
	}
	
	/**
	 * 监听server的状态
	 * 启动结束
	 */
    public void lifeCycleStarted(LifeCycle event) {
    	logger.info("JettyServer启动成功！");
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
