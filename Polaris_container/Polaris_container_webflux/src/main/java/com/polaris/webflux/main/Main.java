package com.polaris.webflux.main;

import com.polaris.http.factory.ContainerDiscoveryHandler;
import com.polaris.http.listener.ServerListener;
import com.polaris.webflux.server.WebfluxServer;

/**
 * 入口启动类
 *
 */
public class Main implements ContainerDiscoveryHandler {
	/**
     * 服务启动
     *
     */
	@Override
	public void start(ServerListener listener) {
    	new Thread(new Runnable() {
			@Override
			public void run() {
				WebfluxServer.getInstance().start(listener);
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
				WebfluxServer.getInstance().stop();
			}
    	}).start();
	}
}
