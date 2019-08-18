package com.polaris.container.jetty.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.container.jetty.server.JettyServer;
import com.polaris.http.factory.ContainerDiscoveryHandler;

/**
 * 入口启动类
 */
public class Main implements ContainerDiscoveryHandler{

	private static Logger logger = LoggerFactory.getLogger(Main.class);
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
