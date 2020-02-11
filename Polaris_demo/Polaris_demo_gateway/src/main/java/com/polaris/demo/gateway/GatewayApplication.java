package com.polaris.demo.gateway;


import com.polaris.container.MainSupport;
import com.polaris.container.annotation.PolarisApplication;

@PolarisApplication
public class GatewayApplication {
	
    public static void main(String[] args) {
    	
    	//启动网关应用
    	MainSupport.startServer(args,GatewayApplication.class);
    }
}
