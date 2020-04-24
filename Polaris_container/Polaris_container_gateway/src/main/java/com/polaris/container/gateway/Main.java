package com.polaris.container.gateway;

import org.springframework.core.annotation.Order;

import com.polaris.container.Server;
import com.polaris.container.ServerOrder;

/**
 * 入口启动类
 *
 */
@Order(ServerOrder.GATEWAY)
public class Main implements Server {
	/**
     * 服务启动
     *
     */
	@Override
	public void start() {
		MainServer.getInstance().start();
	}


}
