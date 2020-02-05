package com.polaris.demo;


import com.polaris.container.annotation.PolarisApplication;
import com.polaris.container.loader.MainSupport;
import com.polaris.container.springboot.annotation.EnablePolarisSpringBoot;
import com.polaris.database.annotation.EnablePolarisDB;

/**
 * 入口启动类
 *
 */
@PolarisApplication
@EnablePolarisSpringBoot
@EnablePolarisDB
public class DemoApplication
{
    
    public static void main( String[] args ) throws Exception
    {
		//启动WEB
    	MainSupport.startServer(args,DemoApplication.class);
    }
}
