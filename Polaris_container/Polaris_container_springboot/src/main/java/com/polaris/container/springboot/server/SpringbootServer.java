package com.polaris.container.springboot.server;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStoppedEvent;

import com.polaris.container.listener.ServerListener;
import com.polaris.core.ConfigurationLoader;
import com.polaris.core.Constant;
import com.polaris.core.config.ConfClient;
import com.polaris.core.config.ConfHandlerEnum;

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
    public void start(ServerListener listener) {
    	
    	//project
    	String projectName = ConfClient.get(Constant.PROJECT_NAME);
    	ConfHandlerEnum.DEFAULT.put(Constant.SPRING_BOOT_NAME, projectName);
    	System.setProperty(Constant.SPRING_BOOT_NAME, projectName);
    	
    	//context
    	String serverContext = ConfClient.get(Constant.SERVER_CONTEXT);
    	ConfHandlerEnum.DEFAULT.put(Constant.SERVER_SPRING_CONTEXT, serverContext);
    	System.setProperty(Constant.SERVER_SPRING_CONTEXT, serverContext);
    	
    	//启动应用
    	SpringApplication springApplication = new SpringApplication(ConfigurationLoader.getRootConfigClass());
        springApplication.addListeners(new ApplicationListener<ContextRefreshedEvent>() {

			@Override
			public void onApplicationEvent(ContextRefreshedEvent event) {
				listener.started();
			}
        	
        });
        
        springApplication.addListeners(new ApplicationListener<ContextStoppedEvent>() {

			@Override
			public void onApplicationEvent(ContextStoppedEvent event) {
				listener.stopped();
			}
        	
        });
        springApplication.run(ConfigurationLoader.getArgs());
    	
    }
    
}
