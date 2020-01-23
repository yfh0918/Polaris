package com.polaris.gateway;


import com.polaris.container.loader.supports.MainSupport;
import com.polaris.core.annotation.PolarisBaseApplication;

@PolarisBaseApplication
public class GatewayApplication {
	
    public static void main(String[] args) {
    	
    	//启动网关应用
    	MainSupport.startServer(args,GatewayApplication.class);
    }
}
