package com.polaris.demo;


import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.polaris.core.Launcher;
import com.polaris.http.supports.MainSupport;

/**
 * 入口启动类
 *
 */
@Configuration
@ComponentScan( basePackages={"com.polaris"})
public class DemoApplication implements Launcher

{
    
    public static void main( String[] args ) throws Exception
    {

		//启动WEB
    	MainSupport.startWebServer(args,null,null);
    }
}
