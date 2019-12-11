package com.polaris.demo;


import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.polaris.core.Launcher;
import com.polaris.dubbo.supports.MainSupport;
@Configuration
@EnableTransactionManagement(proxyTargetClass=true)
public class dubboApplication implements Launcher{
	
	public static void main(String[] args) throws Exception { 
		
		//启动dubbo服务
		MainSupport.startDubboServer(args,new Class[]{dubboApplication.class});
    } 
	

}
