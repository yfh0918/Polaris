package com.polaris.workflow.controller;

import com.polaris.container.ServerRunner;
import com.polaris.container.annotation.PolarisApplication;

@PolarisApplication
public class WorkFlowDubboApplication {
	public static void main(String[] args) throws Exception { 
		ServerRunner.run(args,WorkFlowDubboApplication.class);
    } 
}
