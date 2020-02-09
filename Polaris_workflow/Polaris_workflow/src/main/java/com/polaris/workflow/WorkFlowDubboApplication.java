package com.polaris.workflow;

import com.polaris.container.annotation.PolarisApplication;
import com.polaris.container.loader.MainSupport;

@PolarisApplication
public class WorkFlowDubboApplication {
	public static void main(String[] args) throws Exception { 
		MainSupport.startServer(args,WorkFlowDubboApplication.class);
    } 
}
