package com.polaris.demo;


import com.polaris.container.annotation.PolarisApplication;
import com.polaris.container.loader.MainSupport;

/**
 * 入口启动类
 *
 */
@PolarisApplication
public class DemoApplication
{
    
    public static void main( String[] args ) throws Exception
    {
    	MainSupport.startServer(args,DemoApplication.class);
    }
}
