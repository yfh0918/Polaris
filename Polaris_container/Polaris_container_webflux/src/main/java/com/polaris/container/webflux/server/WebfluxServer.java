package com.polaris.container.webflux.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.web.server.adapter.WebHttpHandlerBuilder;

import com.polaris.container.config.ConfigurationSupport;
import com.polaris.container.listener.ServerListenerSupport;
import com.polaris.core.Constant;
import com.polaris.core.config.ConfClient;
import com.polaris.core.util.SpringUtil;

import reactor.netty.DisposableServer;
import reactor.netty.http.HttpProtocol;
import reactor.netty.http.server.HttpServer;

public class WebfluxServer {
	
	private static Logger logger = LoggerFactory.getLogger(WebfluxServer.class);
	
	/**
     * 私有构造方法
     */
    private WebfluxServer() {
    }
    
    /**
     * 获取单实例公共静态方法
     *
     * @return 单实例
     */
    public static WebfluxServer getInstance() {
        return Singletone.INSTANCE;
    }

    /**
     * 静态内部类实现单例
     */
    private static class Singletone {
        /**
         * 单实例
         */
        private static final WebfluxServer INSTANCE = new WebfluxServer();
    }
    
    /**
     * 启动服务器
     *
     * @throws Exception
     */
    public void start() {

    	//创建context
    	SpringUtil.refresh(ConfigurationSupport.getConfiguration());
    	
    	//通过ApplicationContext创建HttpHandler
        HttpHandler httpHandler = WebHttpHandlerBuilder.applicationContext(SpringUtil.getApplicationContext()).build();
        ReactorHttpHandlerAdapter httpHandlerAdapter = new ReactorHttpHandlerAdapter(httpHandler);
        int port = Integer.parseInt(ConfClient.get(Constant.SERVER_PORT_NAME, Constant.SERVER_PORT_DEFAULT_VALUE));
        DisposableServer server =
                HttpServer.create()
                		  .port(port)
                		  .protocol(new HttpProtocol[] { HttpProtocol.HTTP11 })
                		  .compress(true)
                          .handle(httpHandlerAdapter) 
                          .bindNow();
        logger.info("netty-webflux is started,port:{}",port);
        ServerListenerSupport.started();
        
        // add shutdown hook to stop server
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                	ServerListenerSupport.stopped();
                	server.disposeNow();
                } catch (Exception e) {
                    logger.error("failed to stop netty-webflux.", e);
                }
            }
        });

        //block
        server.onDispose().block();
    }
    
}
