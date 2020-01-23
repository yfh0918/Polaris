package com.polaris.container.dubbo;

import com.polaris.container.Server;
import com.polaris.container.dubbo.server.DubboServer;
import com.polaris.container.servlet.listener.ServerListener;

/**
 * 入口启动类
 *
 */
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
				DubboServer.getInstance().start(listener);
			}
		}).start();
	}

	/**
     * 服务关闭
     *
     */
	@Override
	public void stop() {
	}
}
