package com.polaris.demo;


import com.polaris.http.supports.MainSupport;
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
    	MainSupport.startWebServer(args,DemoApplication.class);
    }
}
