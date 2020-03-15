package com.polaris.container.springboot.server;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStoppedEvent;

import com.polaris.container.config.ConfigurationSupport;
import com.polaris.container.listener.ServerListenerSupport;
import com.polaris.core.Constant;
import com.polaris.core.config.ConfClient;

public class SpringbootServer {
	
	/**
     * 私有构造方法
     */
    private SpringbootServer() {
    }
    
    /**
     * 获取单实例公共静态方法
     *
     * @return 单实例
     */
    public static SpringbootServer getInstance() {
        return Singletone.INSTANCE;
    }

    /**
     * 静态内部类实现单例
     */
    private static class Singletone {
        /**
         * 单实例
         */
        private static final SpringbootServer INSTANCE = new SpringbootServer();
    }
    
    /**
     * 启动服务器
     *
     * @throws Exception
     */
    public void start() {
    	
    	//project
    	String projectName = ConfClient.get(Constant.SPRING_BOOT_NAME, ConfClient.get(Constant.PROJECT_NAME));
    	ConfClient.set(Constant.SPRING_BOOT_NAME, projectName);
    	System.setProperty(Constant.SPRING_BOOT_NAME, projectName);
    	
    	//context
    	String serverContext = ConfClient.get(Constant.SERVER_SPRING_CONTEXT,ConfClient.get(Constant.SERVER_CONTEXT));
    	ConfClient.set(Constant.SERVER_SPRING_CONTEXT, serverContext);
    	System.setProperty(Constant.SERVER_SPRING_CONTEXT, serverContext);
    	
    	//启动应用
    	SpringApplication springApplication = new SpringApplication(ConfigurationSupport.getConfiguration());
        springApplication.addListeners(new ApplicationListener<ContextRefreshedEvent>() {

			@Override
			public void onApplicationEvent(ContextRefreshedEvent event) {
				ServerListenerSupport.started();
			}
        	
        });
        
        springApplication.addListeners(new ApplicationListener<ContextStoppedEvent>() {

			@Override
			public void onApplicationEvent(ContextStoppedEvent event) {
				ServerListenerSupport.stopped();
			}
        	
        });
        springApplication.setBannerMode(Banner.Mode.OFF);
        springApplication.run(ConfigurationSupport.getArgs());
    	
    }
    
}
