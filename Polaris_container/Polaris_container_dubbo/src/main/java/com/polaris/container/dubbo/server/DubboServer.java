package com.polaris.container.dubbo.server;

import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.container.config.ConfigurationHelper;
import com.polaris.container.listener.ServerListenerHelper;
import com.polaris.core.config.ConfClient;
import com.polaris.core.util.SpringUtil;

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
    public void start() {

    	//创建context
    	SpringUtil.refresh(ConfigurationHelper.getConfiguration());
    	
    	//监听
    	ServerListenerHelper.started();
    	
    	// add shutdown hook to stop server
        Runtime.getRuntime().addShutdownHook(jvmShutdownHook);
        
        //block
        try {
        	logger.info("Dubbo started on port(s) " + ConfClient.get("dubbo.protocol.port"));
            new CountDownLatch(1).await();
		} catch (Exception e) {
			ServerListenerHelper.failure();
			logger.error("ERROR:",e);
		}
    }
    
    /**
     * 停止服务服务器
     *
     * @throws Exception
     */
    public void stop() {
    	try {
    		//监听
        	ServerListenerHelper.stopping();
        } catch (Exception e) {
        	// ignore -- IllegalStateException means the VM is already shutting down
        }
    	
    	// remove the shutdown hook that was added when the UndertowServer was started, since it has now been stopped
        try {
            Runtime.getRuntime().removeShutdownHook(jvmShutdownHook);
        } catch (IllegalStateException e) {
            // ignore -- IllegalStateException means the VM is already shutting down
        }

    	try {
    		//监听
        	ServerListenerHelper.stopped();
        } catch (Exception e) {
        	// ignore -- IllegalStateException means the VM is already shutting down
        }
        //log out
        logger.info("Dubbo stopped on port(s) " + ConfClient.get("dubbo.protocol.port"));
    }
    
    /**
     * JVM shutdown hook to shutdown this server. Declared as a class-level variable to allow removing the shutdown hook when the
     * server is stopped normally.
     */
    private final Thread jvmShutdownHook = new Thread(new Runnable() {
        @Override
        public void run() {
            stop();
        }
    }, "Dubbo-JVM-shutdown-hook");
}
