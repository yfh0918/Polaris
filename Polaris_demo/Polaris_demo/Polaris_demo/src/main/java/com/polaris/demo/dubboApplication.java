package com.polaris.demo;


import com.polaris.core.Launcher;
import com.polaris.dubbo.supports.MainSupport;

public class dubboApplication implements Launcher{
	
	public static void main(String[] args) throws Exception { 
		
		//启动dubbo服务
		MainSupport.startDubboServer(args,null);
    } 
	

}
