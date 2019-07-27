package com.polaris.container.jetty.main;

import com.polaris.core.util.LogUtil;
import com.polaris.container.jetty.server.JettyServer;
import com.polaris.http.factory.ContainerDiscoveryHandler;

/**
 * 入口启动类
 */
public class Main implements ContainerDiscoveryHandler{

	private static LogUtil logger = LogUtil.getInstance(Main.class);
    /**
     * 服务启动
     *
     */
	@Override
	public void start() {
		//启动jetty
        new Thread(new Runnable() {
            @Override
            public void run() {
            	logger.info("jetty启动！");
                JettyServer server = JettyServer.getInstance();
                server.start();
            }
        }).start();
		
	}
}
