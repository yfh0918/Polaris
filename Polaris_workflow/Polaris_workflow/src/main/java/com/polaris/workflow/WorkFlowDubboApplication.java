package com.polaris.workflow;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;

import com.polaris.container.annotation.PolarisApplication;
import com.polaris.container.loader.MainSupport;

@PolarisApplication
@EnableDubbo
public class WorkFlowDubboApplication {
	public static void main(String[] args) throws Exception { 
		MainSupport.startServer(args,WorkFlowDubboApplication.class);
    } 
}
