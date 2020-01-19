package com.polaris.gateway;


import com.polaris.core.annotation.PolarisBaseApplication;
import com.polaris.gateway.support.ApplicationSupport;

@PolarisBaseApplication
public class StaticApplication {
    public static void main(String[] args) {
    	    	
    	//启动网关应用
    	ApplicationSupport.startGateway(StaticApplication.class);
    }
}
