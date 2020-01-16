package com.polaris.gateway;


import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.polaris.core.Launcher;
import com.polaris.gateway.support.ApplicationSupport;

@Configuration
@ComponentScan( basePackages={"com.polaris"})
public class GatewayApplication implements Launcher{
	
    public static void main(String[] args) {
    	
    	//启动网关应用
    	ApplicationSupport.startGateway();
    }
}
