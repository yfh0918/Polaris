package com.polaris.gateway;


import com.polaris.core.annotation.PolarisGatewayApplication;
import com.polaris.gateway.support.ApplicationSupport;

@PolarisGatewayApplication
public class StaticApplication {
    public static void main(String[] args) {
    	    	
    	//启动网关应用
    	ApplicationSupport.startGateway(StaticApplication.class);
    }
}
