package com.polaris.demo;


import com.polaris.container.loader.MainSupport;
import com.polaris.container.servlet.springmvc.PolarisSpringMVCApplication;

/**
 * 入口启动类
 *
 */
@PolarisSpringMVCApplication
public class DemoApplication
{
    
    public static void main( String[] args ) throws Exception
    {
		//启动WEB
    	MainSupport.startServer(args, DemoApplication.class);
    }
}
