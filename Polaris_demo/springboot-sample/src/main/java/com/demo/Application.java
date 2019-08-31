package com.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.ContextRefreshedEvent;

import com.polaris.core.config.ConfClient;
import com.polaris.core.naming.NameingClient;

/**
 * 入口启动类
 *
 */
@EnableEurekaServer
@SpringBootApplication
@ComponentScan(basePackages = {"com.demo","com.polaris"})
public class Application {

    public static void main(String[] args) {
    	
    	//配置
    	ConfClient.init();
    	
		//服务启动
        SpringApplication springApplication = new SpringApplication(Application.class);
        springApplication.addListeners(new ApplicationListener<ContextRefreshedEvent>() {

			@Override
			public void onApplicationEvent(ContextRefreshedEvent event) {
				
				//注册中心
		    	NameingClient.register();
			}
        	
        });
        springApplication.run(args);
    }
}
