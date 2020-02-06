package com.polaris.demo;


import com.polaris.container.loader.MainSupport;
import com.polaris.container.springboot.annotation.PolarisSpringBootApplicaiton;

/**
 * 入口启动类
 *
 */
@PolarisSpringBootApplicaiton
public class DemoApplication
{
    
    public static void main( String[] args ) throws Exception
    {
		//启动WEB
    	MainSupport.startServer(args,DemoApplication.class);
    }
}
