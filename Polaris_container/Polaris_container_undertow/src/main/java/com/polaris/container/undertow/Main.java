package com.polaris.container.undertow;

import org.springframework.core.annotation.Order;

import com.polaris.container.Server;
import com.polaris.container.ServerOrder;
import com.polaris.container.undertow.server.UndertowServer;

/**
 * 入口启动类
 */
@Order(ServerOrder.UNDERTOW)
public class Main implements Server{

	
    /**
     * 服务启动
     *
     */
	@Override
	public void start() throws Exception {
        UndertowServer server = UndertowServer.getInstance();
        server.start();
	}
	
    /**
     * 停止启动
     *
     */
	@Override
	public void stop() throws Exception {
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
		return server.getContext();
    }
}
