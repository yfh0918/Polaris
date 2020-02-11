package com.polaris.demo;


import com.polaris.container.MainSupport;
import com.polaris.container.annotation.PolarisApplication;

/**
 * 入口启动类
 *
 */
@PolarisApplication
public class DemoApplication
{
    
    public static void main( String[] args ) throws Exception
    {
		//启动WEB
    	MainSupport.startServer(args, DemoApplication.class);
    }
}
