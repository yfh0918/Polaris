package com.polaris.workflow;

import com.polaris.container.MainSupport;
import com.polaris.container.annotation.PolarisApplication;

@PolarisApplication
public class WorkFlowDubboApplication {
	public static void main(String[] args) throws Exception { 
		MainSupport.startServer(args,WorkFlowDubboApplication.class);
    } 
}
