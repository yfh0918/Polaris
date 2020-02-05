package com.polaris.demo;


import com.polaris.container.loader.MainSupport;
import com.polaris.container.springboot.annotation.PolarisSpringBootApplicaiton;
import com.polaris.database.annotation.EnablePolarisDB;

/**
 * 入口启动类
 *
 */
@PolarisSpringBootApplicaiton
@EnablePolarisDB
public class DemoApplication
{
    
    public static void main( String[] args ) throws Exception
    {
		//启动WEB
    	MainSupport.startServer(args,DemoApplication.class);
    }
}
