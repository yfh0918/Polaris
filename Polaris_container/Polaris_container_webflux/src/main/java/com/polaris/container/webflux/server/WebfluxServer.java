package com.polaris.container.webflux.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.server.adapter.WebHttpHandlerBuilder;

import com.polaris.container.config.ConfigurationSupport;
import com.polaris.container.listener.ServerListenerSupport;
import com.polaris.core.Constant;
import com.polaris.core.config.ConfClient;
import com.polaris.core.util.SpringUtil;

import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
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
    	SpringUtil.refresh(ConfigurationSupport.getConfiguration(WebFluxConfiguration.class));
    	
    	//通过ApplicationContext创建HttpHandler
        HttpHandler httpHandler = WebHttpHandlerBuilder.applicationContext(SpringUtil.getApplicationContext()).build();
        ReactorHttpHandlerAdapter httpHandlerAdapter = new ReactorHttpHandlerAdapter(httpHandler);
        int port = Integer.parseInt(ConfClient.get(Constant.SERVER_PORT_NAME, Constant.SERVER_PORT_DEFAULT_VALUE));
        HttpServer server =
                HttpServer.create()
                		  .port(port)
                		  .protocol(new HttpProtocol[] { HttpProtocol.HTTP11 })
                		  .compress(Boolean.parseBoolean(ConfClient.get("server.compress","true")))//压缩
                          .handle(httpHandlerAdapter);
        boolean ssl = Boolean.parseBoolean(ConfClient.get("server.ssl","false"));
        boolean http2 = Boolean.parseBoolean(ConfClient.get("server.http2","false"));
        
        //设置ssl
        if (ssl) {
        	try {
            	SelfSignedCertificate cert = new SelfSignedCertificate();
        	 	SslContextBuilder sslContextBuilder =
        	 	SslContextBuilder.forServer(cert.certificate(), cert.privateKey());
            	server.secure(sslContextSpec -> sslContextSpec.sslContext(sslContextBuilder));
        	} catch (Exception ex) {
        		logger.info("netty-webflux start error : {}",ex);
        		return;
        	}
        }
        
        //设置协议
        server.protocol(listProtocols(ssl, http2));
        
        //绑定服务
        DisposableServer disposableSever = server.bindNow();
        logger.info("netty-webflux is started,port:{}",port);
        ServerListenerSupport.started();
        
        // add shutdown hook to stop server
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                	ServerListenerSupport.stopped();
                	disposableSever.disposeNow();
                } catch (Exception e) {
                    logger.error("failed to stop netty-webflux.", e);
                }
            }
        });

        //block
        disposableSever.onDispose().block();
    }
    
    private HttpProtocol[] listProtocols(boolean ssl, boolean http2) {
		if (http2) {
			if (ssl) {
				return new HttpProtocol[] { HttpProtocol.H2, HttpProtocol.HTTP11 };
			}
			else {
				return new HttpProtocol[] { HttpProtocol.H2C, HttpProtocol.HTTP11 };
			}
		}
		return new HttpProtocol[] { HttpProtocol.HTTP11 };
	}
    
    @EnableWebFlux
    @Configuration 
    protected static class WebFluxConfiguration {
	}
}
