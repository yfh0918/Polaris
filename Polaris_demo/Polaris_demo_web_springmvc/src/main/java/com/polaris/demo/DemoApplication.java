package com.polaris.demo;


import com.polaris.container.ServerRunner;
import com.polaris.container.annotation.PolarisApplication;

/**
 * 入口启动类
 *
 */
@PolarisApplication
public class DemoApplication
{
    
    public static void main( String[] args ) throws Exception
    {
    	ServerRunner.run(args,DemoApplication.class);
    }
}
