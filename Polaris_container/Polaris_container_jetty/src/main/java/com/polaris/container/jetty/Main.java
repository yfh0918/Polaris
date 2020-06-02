package com.polaris.container.jetty;

import org.springframework.core.annotation.Order;

import com.polaris.container.Server;
import com.polaris.container.ServerOrder;
import com.polaris.container.jetty.server.JettyServer;

/**
 * 入口启动类
 */
@Order(ServerOrder.JETTY)
public class Main implements Server{

    /**
     * 服务启动
     *
     */
	@Override
	public void start() throws Exception{
        JettyServer server = JettyServer.getInstance();
        server.start();
	}
	
    /**
     * 停止启动
     *
     */
	@Override
	public void stop() throws Exception{
        JettyServer server = JettyServer.getInstance();
        server.stop();
	}
	
	/**
     * servlet上下文
     *
     */
	@Override
	public Object getContext() {
		JettyServer server = JettyServer.getInstance();
		return server.getContext();
    }
}
