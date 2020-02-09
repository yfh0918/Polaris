package com.polaris.demo;


import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;

import com.polaris.container.annotation.PolarisApplication;
import com.polaris.container.loader.MainSupport;

@PolarisApplication
@EnableDubbo
public class DubboApplication{
	
	public static void main(String[] args) throws Exception { 
		
		//启动dubbo服务
		MainSupport.startServer(args,DubboApplication.class);
    } 
	

}
