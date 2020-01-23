package com.polaris.container.tomcat;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.container.tomcat.server.TomcatServer;
import com.polaris.server.factory.ContainerDiscoveryHandler;
import com.polaris.server.listener.ServerListener;

/**
 * 入口启动类
 *
 */
public class Main implements ContainerDiscoveryHandler {

	private static Logger logger = LoggerFactory.getLogger(Main.class);
	
	/**
     * 服务启动
     *
     */
	@Override
	public void start(ServerListener listener) {
    	new Thread(new Runnable() {
			@Override
			public void run() {
				logger.info("tomcat启动！");
				TomcatServer server = TomcatServer.getInstance();
				server.start(listener);
			}
		}).start();
	}

	/**
     * 服务关闭
     *
     */
	@Override
	public void stop() {
    	new Thread(new Runnable() {
			@Override
			public void run() {
				logger.info("tomcat关闭！");
				TomcatServer server = TomcatServer.getInstance();
		        server.stop();
			}
    	}).start();
	}
	
	/**
     * servlet上下文
     *
     */
	@Override
	public ServletContext getServletContex() {
		TomcatServer server = TomcatServer.getInstance();
		return server.getServletContex();
    }
		
}
