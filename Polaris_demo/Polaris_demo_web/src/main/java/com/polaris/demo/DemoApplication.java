package com.polaris.demo;


import com.polaris.core.annotation.PolarisApplication;
import com.polaris.http.supports.MainSupport;

/**
 * 入口启动类
 *
 */
@PolarisApplication(scanBasePackages={"com.polaris"})
public class DemoApplication

{
    
    public static void main( String[] args ) throws Exception
    {

		//启动WEB
    	MainSupport.startWebServer(args,new Class[]{DemoApplication.class});
    }
}
