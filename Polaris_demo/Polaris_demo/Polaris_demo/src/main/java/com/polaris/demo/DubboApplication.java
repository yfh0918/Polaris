package com.polaris.demo;


import com.polaris.container.ServerRunner;
import com.polaris.container.annotation.PolarisApplication;

@PolarisApplication
public class DubboApplication{
	
	public static void main(String[] args) throws Exception { 
		
		//启动dubbo服务
		ServerRunner.run(args,DubboApplication.class);
    } 
	

}
