package com.polaris.demo;


import com.polaris.container.annotation.PolarisApplication;
import com.polaris.container.loader.MainSupport;
import com.polaris.container.springboot.annotation.EnablePolarisSpringboot;

/**
 * 入口启动类
 *
 */
@PolarisApplication
@EnablePolarisSpringboot
public class DemoApplication
{
    
    public static void main( String[] args ) throws Exception
    {
		//启动WEB
    	MainSupport.startServer(args,DemoApplication.class);
    }
}
