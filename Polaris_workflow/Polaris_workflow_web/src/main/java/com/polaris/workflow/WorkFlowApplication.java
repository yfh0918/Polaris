package com.polaris.workflow;

import com.polaris.container.ServerRunner;
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
    	ServerRunner.startServer(args,WorkFlowApplication.class);
    }
}
