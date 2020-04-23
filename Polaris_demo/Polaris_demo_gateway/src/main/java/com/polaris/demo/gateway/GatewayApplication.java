package com.polaris.demo.gateway;


import com.polaris.container.ServerRunner;
import com.polaris.container.annotation.PolarisApplication;
import com.polaris.container.gateway.HttpFilterEnum;
import com.polaris.container.listener.ServerListener;
import com.polaris.demo.gateway.request.TokenExtendHttpRequestFilter;
import com.polaris.demo.gateway.response.TokenExtendHttpResponseFilter;

@PolarisApplication
public class GatewayApplication {
	
    public static void main(String[] args) {
    	
    	//启动网关应用
    	ServerRunner.run(args,GatewayApplication.class, new ServerListener() {
    		@Override
    		public void started() {
    			HttpFilterEnum.replaceFilter(HttpFilterEnum.Token, new TokenExtendHttpRequestFilter());
    			HttpFilterEnum.replaceFilter(HttpFilterEnum.TokenResponse, new TokenExtendHttpResponseFilter());
    		}
    	});
    }
}
