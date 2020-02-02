package com.polaris.demo;


import com.polaris.container.annotation.PolarisApplication;
import com.polaris.container.loader.MainSupport;

@PolarisApplication
public class DubboApplication{
	
	public static void main(String[] args) throws Exception { 
		
		//启动dubbo服务
		MainSupport.startServer(args,DubboApplication.class);
    } 
	

}
