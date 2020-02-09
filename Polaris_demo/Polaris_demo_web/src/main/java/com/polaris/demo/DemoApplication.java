package com.polaris.demo;


import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;

import com.polaris.container.annotation.PolarisApplication;
import com.polaris.container.loader.MainSupport;

/**
 * 入口启动类
 *
 */
@PolarisApplication
@EnableDubbo
public class DemoApplication

{
    
    public static void main( String[] args ) throws Exception
    {

		//启动WEB
    	MainSupport.startServer(args,DemoApplication.class);
    }
}
