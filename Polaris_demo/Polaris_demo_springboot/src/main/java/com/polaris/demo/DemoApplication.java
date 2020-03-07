package com.polaris.demo;


import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.polaris.container.ServerRunner;

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
    	ServerRunner.run(args,DemoApplication.class);
    }
}
