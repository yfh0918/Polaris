package com.polaris.container.gateway;

import org.springframework.core.annotation.Order;

import com.polaris.container.Server;
import com.polaris.container.gateway.server.GatewayServer;
import com.polaris.container.servlet.listener.ServerListener;

/**
 * 入口启动类
 *
 */
@Order(3)
public class Main implements Server {
	/**
     * 服务启动
     *
     */
	@Override
	public void start(ServerListener listener) {
    	new Thread(new Runnable() {
			@Override
			public void run() {
				GatewayServer.getInstance().start(listener);
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
				GatewayServer.getInstance().stop();
			}
    	}).start();
	}
}
