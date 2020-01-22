package com.polaris.workflow;

import com.polaris.dubbo.annotation.PolarisDubboApplication;
import com.polaris.loader.supports.MainSupport;

@PolarisDubboApplication
public class WorkFlowDubboApplication {
	public static void main(String[] args) throws Exception { 
		MainSupport.startServer(args,WorkFlowDubboApplication.class);
    } 
}
