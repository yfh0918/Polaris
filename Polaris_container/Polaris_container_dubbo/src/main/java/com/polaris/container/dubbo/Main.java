package com.polaris.container.dubbo;

import org.springframework.core.annotation.Order;

import com.polaris.container.Server;
import com.polaris.container.ServerOrder;
import com.polaris.container.dubbo.server.DubboServer;

/**
 * 入口启动类
 *
 */
@Order(ServerOrder.DUBBO)
public class Main implements Server {
	/**
     * 服务启动
     *
     */
	@Override
	public void start() {
    	new Thread(new Runnable() {
			@Override
			public void run() {
				DubboServer.getInstance().start();
			}
		}).start();
	}

}
