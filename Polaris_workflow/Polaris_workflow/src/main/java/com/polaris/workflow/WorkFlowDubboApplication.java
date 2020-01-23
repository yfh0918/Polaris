package com.polaris.workflow;

import com.polaris.container.dubbo.annotation.PolarisDubboApplication;
import com.polaris.container.loader.supports.MainSupport;

@PolarisDubboApplication
public class WorkFlowDubboApplication {
	public static void main(String[] args) throws Exception { 
		MainSupport.startServer(args,WorkFlowDubboApplication.class);
    } 
}
