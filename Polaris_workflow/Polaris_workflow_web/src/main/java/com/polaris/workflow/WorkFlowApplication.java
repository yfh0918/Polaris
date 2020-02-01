package com.polaris.workflow;

import com.polaris.container.annotation.PolarisWebApplication;
import com.polaris.container.loader.MainSupport;

/**
 * 入口启动类
 *
 */
@PolarisWebApplication
public class WorkFlowApplication
{
    
    public static void main( String[] args ) throws Exception
    {

		//启动WEB
    	MainSupport.startServer(args,WorkFlowApplication.class);
    }
}
