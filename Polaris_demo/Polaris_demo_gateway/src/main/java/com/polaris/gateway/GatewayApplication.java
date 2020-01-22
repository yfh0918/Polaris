package com.polaris.gateway;


import com.polaris.core.annotation.PolarisBaseApplication;
import com.polaris.http.supports.MainSupport;

@PolarisBaseApplication
public class GatewayApplication {
	
    public static void main(String[] args) {
    	
    	//启动网关应用
    	MainSupport.startWebServer(args,GatewayApplication.class);
    }
}
