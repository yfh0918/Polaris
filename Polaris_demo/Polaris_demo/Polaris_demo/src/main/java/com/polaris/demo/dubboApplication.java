package com.polaris.demo;


import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.polaris.core.Launcher;
import com.polaris.dubbo.supports.MainSupport;

@Configuration
@EnableDubbo(scanBasePackages = "com.polaris.demo.core.entry")
@PropertySource("classpath:/config/application.properties")
public class dubboApplication implements Launcher{
	
	public static void main(String[] args) throws Exception { 
		
		//启动dubbo服务
		MainSupport.startDubboServer(args,dubboApplication.class);
    } 
	

}
