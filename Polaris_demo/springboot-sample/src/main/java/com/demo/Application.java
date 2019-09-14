package com.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStoppedEvent;

import com.polaris.core.config.ConfClient;
import com.polaris.core.naming.NameingClient;

/**
 * 入口启动类
 *
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.demo","com.polaris"})
public class Application {

    public static void main(String[] args) {
    	
    	//配置
    	ConfClient.init();
    	
		//服务启动
        SpringApplication springApplication = new SpringApplication(Application.class);
        
        //注册服务
        springApplication.addListeners(new ApplicationListener<ContextRefreshedEvent>() {

			@Override
			public void onApplicationEvent(ContextRefreshedEvent event) {
				
				//注册中心
		    	NameingClient.register();
			}
        	
        });
        
        //注销服务
        springApplication.addListeners(new ApplicationListener<ContextStoppedEvent>() {

			@Override
			public void onApplicationEvent(ContextStoppedEvent event) {
				
				//注册中心
		    	NameingClient.unRegister();
			}
        	
        });
        
        //启动服务
        springApplication.run(args);
    }
}
