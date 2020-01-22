package com.polaris.demo;


import com.polaris.webflux.annotation.PolarisWebfluxApplication;
import com.polaris.webflux.supports.MainSupport;

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
    	MainSupport.startWebflux(args,DemoApplication.class);
    }
}
