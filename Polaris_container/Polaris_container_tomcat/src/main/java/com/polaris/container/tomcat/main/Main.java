package com.polaris.container.tomcat.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.container.tomcat.server.TomcatServer;
import com.polaris.http.factory.ContainerDiscoveryHandler;
import com.polaris.http.supports.ServerListener;

/**
 * 入口启动类
 *
 */
public class Main implements ContainerDiscoveryHandler {

	private static Logger logger = LoggerFactory.getLogger(Main.class);
	/**
     * 服务启动
     *
     */
	@Override
	public void start(ServerListener listener) {
		//启动tomcat
    	new Thread(new Runnable() {
			@Override
			public void run() {
				logger.info("tomcat启动！");
				TomcatServer server = TomcatServer.getInstance();
				server.start(listener);
			}
		}).start();
	}


}
