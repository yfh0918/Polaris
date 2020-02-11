package com.polaris.workflow;

import com.polaris.container.MainSupport;
import com.polaris.container.annotation.PolarisApplication;

/**
 * 入口启动类
 *
 */
@PolarisApplication
public class WorkFlowApplication
{
    
    public static void main( String[] args ) throws Exception
    {

		//启动WEB
    	MainSupport.startServer(args,WorkFlowApplication.class);
    }
}
