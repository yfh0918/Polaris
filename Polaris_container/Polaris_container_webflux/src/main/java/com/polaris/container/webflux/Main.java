package com.polaris.container.webflux;

import org.springframework.core.annotation.Order;

import com.polaris.container.Server;
import com.polaris.container.ServerOrder;
import com.polaris.container.webflux.server.WebfluxServer;

/**
 * 入口启动类
 *
 */
@Order(ServerOrder.WEBFLUX)
public class Main implements Server {
	
	/**
     * 服务启动
     *
     */
	@Override
	public void start() {
		WebfluxServer.getInstance().start();
	}
	
	/**
     * 停止启动
     *
     */
	@Override
	public void stop() {
		WebfluxServer.getInstance().stop();
	}
}
