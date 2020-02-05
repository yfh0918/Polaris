package com.polaris.demo.gateway;


import com.polaris.container.annotation.PolarisApplication;
import com.polaris.container.loader.MainSupport;

@PolarisApplication
public class GatewayApplication {
	
    public static void main(String[] args) {
    	
    	//启动网关应用
    	MainSupport.startServer(args,GatewayApplication.class);
    }
}
