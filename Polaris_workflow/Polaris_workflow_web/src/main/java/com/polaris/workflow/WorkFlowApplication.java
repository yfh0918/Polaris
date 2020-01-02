package com.polaris.workflow;

import com.polaris.core.annotation.PolarisApplication;
import com.polaris.http.supports.MainSupport;

/**
 * 入口启动类
 *
 */
@PolarisApplication(scanBasePackages={"com.polaris"})
public class WorkFlowApplication
{
    
    public static void main( String[] args ) throws Exception
    {

		//启动WEB
    	MainSupport.startWebServer(args,new Class[]{WorkFlowApplication.class});
    }
}
