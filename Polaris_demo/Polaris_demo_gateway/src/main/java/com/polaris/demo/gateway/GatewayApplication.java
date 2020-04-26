package com.polaris.demo.gateway;


import com.polaris.container.ServerRunner;
import com.polaris.container.annotation.PolarisApplication;
import com.polaris.container.gateway.HttpFilterEnum;
import com.polaris.container.gateway.HttpFilterHelper;
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
    			HttpFilterHelper.INSTANCE.replaceFilter(HttpFilterEnum.Token.getFilterEntity(), new TokenExtendHttpRequestFilter());
    			HttpFilterHelper.INSTANCE.replaceFilter(HttpFilterEnum.TokenResponse.getFilterEntity(), new TokenExtendHttpResponseFilter());
    		}
    	});
    }
}
