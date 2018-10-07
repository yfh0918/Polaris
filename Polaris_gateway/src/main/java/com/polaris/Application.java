package com.polaris;

import com.polaris.comm.config.ConfClient;
import com.polaris.gateway.support.ApplicationSupport;

public class Application {
    public static void main(String[] args) {
    	
    	//载入spring
    	ConfClient.setAppName("Polaris_gateway");

    	//启动网关应用
    	ApplicationSupport.startGateway();
    }
}
