package com.polaris.container.springboot;

import org.springframework.core.annotation.Order;

import com.polaris.container.Server;
import com.polaris.container.ServerOrder;
import com.polaris.container.springboot.server.SpringbootServer;

/**
 * 入口启动类
 *
 */
@Order(ServerOrder.SPRINGBOOT)
public class Main implements Server {
	
	/**
     * 服务启动
     *
     */
	@Override
	public void start() throws Exception {
		SpringbootServer.getInstance().start();
	}

	/**
     * 停止服务
     *
     */
	@Override
	public void stop() throws Exception {
		SpringbootServer.getInstance().stop();
	}
	
	/**
     * 获取上下文
     *
     */
	@Override
	public Object getContext() {
		return SpringbootServer.getInstance().getContext();
	}
}
