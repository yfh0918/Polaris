package com.polaris.dubbo;

import com.polaris.dubbo.server.DubboServer;
import com.polaris.server.factory.Container;
import com.polaris.server.listener.ServerListener;

/**
 * 入口启动类
 *
 */
public class Main implements Container {
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
