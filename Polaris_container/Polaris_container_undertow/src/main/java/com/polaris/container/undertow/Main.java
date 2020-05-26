package com.polaris.container.undertow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;

import com.polaris.container.Server;
import com.polaris.container.ServerOrder;
import com.polaris.container.undertow.server.UndertowServer;

/**
 * 入口启动类
 */
@Order(ServerOrder.UNDERTOW)
public class Main implements Server{

	private static Logger logger = LoggerFactory.getLogger(Main.class);
	
    /**
     * 服务启动
     *
     */
	@Override
	public void start() {
    	logger.info("undertow启动！");
        UndertowServer server = UndertowServer.getInstance();
        server.start();
	}
	
    /**
     * 停止启动
     *
     */
	@Override
	public void stop() {
    	logger.info("undertow停止！");
        UndertowServer server = UndertowServer.getInstance();
        server.stop();
	}
	
	/**
     * servlet上下文
     *
     */
	@Override
	public Object getContext() {
		UndertowServer server = UndertowServer.getInstance();
		return server.getServletContex();
    }
}
