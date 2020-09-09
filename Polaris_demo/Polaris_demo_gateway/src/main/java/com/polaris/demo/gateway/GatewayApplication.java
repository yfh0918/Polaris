package com.polaris.demo.gateway;


import com.polaris.container.ServerRunner;
import com.polaris.container.annotation.PolarisApplication;
import com.polaris.container.gateway.HttpFilterHelper;
import com.polaris.container.gateway.pojo.HttpFilterEntity;
import com.polaris.container.listener.ServerListener;
import com.polaris.core.component.LifeCycle;
import com.polaris.demo.gateway.request.HttpCCRequestFilter;
import com.polaris.demo.gateway.request.HttpDegradeRequestFilter;

@PolarisApplication
public class GatewayApplication {
	
    public static void main(String[] args) throws Exception {
    	
    	//启动网关应用
    	ServerRunner.run(args,GatewayApplication.class, new ServerListener() {
    		@Override
    		public void started(LifeCycle event) {
    		    //HttpFilterHelper.removeFilter(HttpFilterEntityEnum.Args.getFilterEntity());
                //HttpFilterHelper.addFilter(new HttpFilterEntity(new HttpDegradeRequestFilter(), 1));
                //HttpFilterHelper.addFilter(new HttpFilterEntity(new HttpCCRequestFilter(), 8));
    			//HttpFilterHelper.addFilter(new HttpFilterEntity(new HttpTokenRequestFilter(), 24));
                //HttpFilterHelper.addFilter(new HttpFilterEntity(new HttpTokenResponseFilter(), 1));
    		}
    	});
    }
}
