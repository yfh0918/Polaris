package com.polaris.demo;


import com.polaris.dubbo.annotation.PolarisDubboApplication;
import com.polaris.loader.supports.MainSupport;

@PolarisDubboApplication
public class DubboApplication{
	
	public static void main(String[] args) throws Exception { 
		
		//启动dubbo服务
		MainSupport.startServer(args,DubboApplication.class);
    } 
	

}
