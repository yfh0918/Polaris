package com.polaris.demo;


import com.polaris.core.annotation.PolarisApplication;
import com.polaris.dubbo.supports.MainSupport;

@PolarisApplication(scanBasePackages={"com.polaris"})
public class dubboApplication{
	
	public static void main(String[] args) throws Exception { 
		
		//启动dubbo服务
		MainSupport.startDubboServer(args,new Class[]{dubboApplication.class});
    } 
	

}
