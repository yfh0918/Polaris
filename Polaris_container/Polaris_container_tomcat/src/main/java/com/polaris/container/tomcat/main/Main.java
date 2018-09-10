package com.polaris.container.tomcat.main;

import com.polaris.container.tomcat.server.TomcatServer;

/**
 * 入口启动类
 *
 */
public class Main 
{

    /**
     * 构造函数
     * @Title: getInstance
     * @return  唯一进程实例
     */
    public Main() {
    	
    	//启动tomcat
    	new Thread(new Runnable() {
			@Override
			public void run() {
				TomcatServer server = TomcatServer.getInstance();
				server.start();
			}
		}).start();

	}


}
