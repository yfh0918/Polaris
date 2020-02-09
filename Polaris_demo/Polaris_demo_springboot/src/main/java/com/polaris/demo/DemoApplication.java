package com.polaris.demo;


import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.polaris.container.loader.MainSupport;

/**
 * 入口启动类
 *
 */
@SpringBootApplication
public class DemoApplication
{
    
    public static void main( String[] args ) throws Exception
    {
		//启动WEB
    	MainSupport.startServer(args,DemoApplication.class);
    }
}
