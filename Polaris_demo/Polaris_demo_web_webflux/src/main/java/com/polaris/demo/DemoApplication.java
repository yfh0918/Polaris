package com.polaris.demo;


import com.polaris.loader.supports.MainSupport;
import com.polaris.webflux.annotation.PolarisWebfluxApplication;

/**
 * 入口启动类
 *
 */
@PolarisWebfluxApplication
public class DemoApplication
{
    
    public static void main( String[] args ) throws Exception
    {
		//启动WEB
    	MainSupport.startServer(args,DemoApplication.class);
    }
}
