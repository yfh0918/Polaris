package com.polaris.container.tomcat;

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

	/**
     * 服务启动
     *
     */
	@Override
	public void start() throws Exception{
		TomcatServer server = TomcatServer.getInstance();
		server.start();
	}
	
	/**
     * 停止启动
     *
     */
	@Override
	public void stop() throws Exception{
		TomcatServer server = TomcatServer.getInstance();
		server.stop();
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
