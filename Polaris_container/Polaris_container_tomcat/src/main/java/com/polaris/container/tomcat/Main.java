package com.polaris.container.tomcat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;

import com.polaris.container.Server;
import com.polaris.container.ServerOrder;
import com.polaris.container.tomcat.server.TomcatServer;

/**
 * 入口启动类
 *
 */
@Order(ServerOrder.TOMCAT)
public class Main implements Server {

	private static Logger logger = LoggerFactory.getLogger(Main.class);
	
	/**
     * 服务启动
     *
     */
	@Override
	public void start() {
		logger.info("tomcat启动！");
		TomcatServer server = TomcatServer.getInstance();
		server.start();
	}
	
	/**
     * servlet上下文
     *
     */
	@Override
	public Object getContext() {
		TomcatServer server = TomcatServer.getInstance();
		return server.getServletContex();
    }
		
}
