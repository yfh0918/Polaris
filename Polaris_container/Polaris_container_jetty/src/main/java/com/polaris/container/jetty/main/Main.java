package com.polaris.container.jetty.main;

import com.polaris.container.jetty.server.JettyServer;

/**
 * 入口启动类
 */
public class Main {


    /**
     * 构造函数
     *
     * @return 唯一进程实例
     * @Title: getInstance
     */
    public Main() {

        //启动jetty
        new Thread(new Runnable() {
            @Override
            public void run() {
                JettyServer server = JettyServer.getInstance();
                server.start();
            }
        }).start();

    }
}
