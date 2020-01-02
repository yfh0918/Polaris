package com.polaris.gateway;


import com.polaris.core.Launcher;
import com.polaris.core.annotation.PolarisApplication;
import com.polaris.gateway.support.ApplicationSupport;

@PolarisApplication(scanBasePackages={"com.polaris"})
public class GatewayApplication implements Launcher{
	
    public static void main(String[] args) {
    	
    	//启动网关应用
    	ApplicationSupport.startGateway(GatewayApplication.class);
    }
}
