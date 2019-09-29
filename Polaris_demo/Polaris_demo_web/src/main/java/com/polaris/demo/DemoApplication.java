package com.polaris.demo;


import com.polaris.core.Launcher;
import com.polaris.http.supports.MainSupport;

/**
 * 入口启动类
 *
 */
public class DemoApplication implements Launcher

{
    
    public static void main( String[] args ) throws Exception
    {

		//启动WEB
    	MainSupport.startWebServer(args);
    }
}
