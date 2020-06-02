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
	public void start() throws Exception {
		HttpServer.getInstance().start();
	}

	/**
     * 停止启动
     *
     */
	@Override
	public void stop() throws Exception {
		HttpServer.getInstance().stop();
	}
	
	/**
     * 获取上下文
     *
     */
	@Override
	public Object getContext() {
		return HttpServer.getInstance().getContext();
	}
}
