package com.polaris.workflow;

import com.polaris.core.annotation.PolarisApplication;
import com.polaris.dubbo.supports.MainSupport;

@PolarisApplication(scanBasePackages={"com.polaris"})
public class WorkFlowDubboApplication {
	public static void main(String[] args) throws Exception { 
		MainSupport.startDubboServer(args,new Class[]{WorkFlowDubboApplication.class});
    } 
}
