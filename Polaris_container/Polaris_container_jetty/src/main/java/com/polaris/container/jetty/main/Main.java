package com.polaris.container.jetty.main;

import com.polaris.comm.util.LogUtil;
import com.polaris.container.jetty.listener.ServerHandlerListerner;
import com.polaris.container.jetty.listener.ServerListener;
import com.polaris.container.jetty.server.JettyServer;

/**
 * 入口启动类
 */
public class Main implements ServerListener {

    /**
     * 单例
     */
    private static final LogUtil logger = LogUtil.getInstance(Main.class);

    /**
     * 构造函数
     *
     * @return 唯一进程实例
     * @Title: getInstance
     */
    public Main() {

        //注册事件
        ServerHandlerListerner.getInstance().addListener(this);

        //启动jetty
        new Thread(new Runnable() {
            @Override
            public void run() {
                JettyServer server = JettyServer.getInstance();
                server.start();
            }
        }).start();

    }

    //注册服务（回调）
    @Override
    public void onServerStatusChanged(int serverStatus) {
        switch (serverStatus) {

            //已经启动
            case SERVER_STATUS_STARTED:
                logger.info("JettyServer启动成功！");
                break;

            //已经停止
            case SERVER_STATUS_STOPPED:
                logger.info("JettyServer已经停止！");
                break;

            //异常
            default:
                logger.info("JettyServer启动失败！");
                break;
        }
    }
}
