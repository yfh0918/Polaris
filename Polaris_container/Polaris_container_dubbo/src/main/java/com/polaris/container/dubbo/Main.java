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
	public void start() throws Exception  {
		DubboServer.getInstance().start();
	}

	/**
     * 停止启动
     *
     */
	@Override
	public void stop() throws Exception  {
		DubboServer.getInstance().stop();
	}
	
	/**
     * 获取上下文context
     *
     */
	@Override
	public Object getContext() {
		return DubboServer.getInstance().getContext();
	}
}
