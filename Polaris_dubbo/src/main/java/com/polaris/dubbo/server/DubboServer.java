package com.polaris.dubbo.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.core.util.SpringUtil;
import com.polaris.http.listener.ServerListener;

public class DubboServer {
	
	private static Logger logger = LoggerFactory.getLogger(DubboServer.class);
	
	/**
     * 私有构造方法
     */
    private DubboServer() {
    }
    
    /**
     * 获取单实例公共静态方法
     *
     * @return 单实例
     */
    public static DubboServer getInstance() {
        return Singletone.INSTANCE;
    }

    /**
     * 静态内部类实现单例
     */
    private static class Singletone {
        /**
         * 单实例
         */
        private static final DubboServer INSTANCE = new DubboServer();
    }
    
    /**
     * 启动服务器
     *
     * @throws Exception
     */
    public void start(ServerListener listener) {

    	//创建context
    	SpringUtil.refresh();
    	listener.started(null);
    	
    	// add shutdown hook to stop server
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                	listener.stopped(null);
                } catch (Exception e) {
                    logger.error("failed to stop dubbo.", e);
                }
            }
        });
    }
}
