package com.polaris.demo;


import com.polaris.container.ServerRunner;
import com.polaris.container.annotation.PolarisApplication;
import com.polaris.container.listener.ServerListener;

/**
 * 入口启动类
 *
 */
@PolarisApplication
public class DemoApplication
{
    
    public static void main( String[] args ) throws Exception
    {
    	ServerRunner.run(args,DemoApplication.class, new ServerListener() {
    		@Override
    		public void started() {
    			DemoLifeCycle lifeCycle = new DemoLifeCycle();
    			lifeCycle.start();
    		}
    	});
    }
}
