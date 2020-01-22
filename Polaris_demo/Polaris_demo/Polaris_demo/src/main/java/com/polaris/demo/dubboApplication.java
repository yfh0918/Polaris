package com.polaris.demo;


import com.polaris.dubbo.annotation.PolarisDubboApplication;
import com.polaris.http.supports.MainSupport;

@PolarisDubboApplication
public class dubboApplication{
	
	public static void main(String[] args) throws Exception { 
		
		//启动dubbo服务
		MainSupport.startServer(args,dubboApplication.class);
    } 
	

}
