package com.polaris.demo;


import com.polaris.core.annotation.PolarisApplication;
import com.polaris.http.supports.MainSupport;

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
