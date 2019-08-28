package com.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

import com.polaris.core.config.ConfClient;
import com.polaris.core.naming.NameingClient;

/**
 * 入口启动类
 *
 */
@EnableEurekaServer
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
    	
    	//配置
    	ConfClient.init(null);
    	
    	//注册中心
    	NameingClient.register();
    	
		//服务启动
        SpringApplication.run(Application.class, args);
    }
}
