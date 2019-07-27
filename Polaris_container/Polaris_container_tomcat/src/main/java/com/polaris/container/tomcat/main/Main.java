package com.polaris.container.tomcat.main;

import com.polaris.core.util.LogUtil;
import com.polaris.container.tomcat.server.TomcatServer;
import com.polaris.http.factory.ContainerDiscoveryHandler;

/**
 * 入口启动类
 *
 */
public class Main implements ContainerDiscoveryHandler {

	private static LogUtil logger = LogUtil.getInstance(Main.class);
	/**
     * 服务启动
     *
     */
	@Override
	public void start() {
		//启动tomcat
    	new Thread(new Runnable() {
			@Override
			public void run() {
				logger.info("tomcat启动！");
				TomcatServer server = TomcatServer.getInstance();
				server.start();
			}
		}).start();
	}


}
