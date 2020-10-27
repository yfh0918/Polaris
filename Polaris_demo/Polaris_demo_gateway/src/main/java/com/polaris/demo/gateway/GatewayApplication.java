
package com.polaris.demo.gateway;


import com.polaris.container.ServerRunner;
import com.polaris.container.annotation.PolarisApplication;
import com.polaris.container.gateway.HttpFilterHelper;
import com.polaris.container.gateway.HttpFilterInit;
import com.polaris.container.gateway.pojo.HttpFile;
import com.polaris.container.gateway.pojo.HttpFilterEntity;
import com.polaris.container.listener.ServerListener;
import com.polaris.core.component.LifeCycle;
import com.polaris.demo.gateway.request.EchoEndpoint;

@PolarisApplication
public class GatewayApplication {
	
    public static void main(String[] args) throws Exception {
    	
    	//启动网关应用
    	ServerRunner.run(args,GatewayApplication.class, new ServerListener() {
    	    @Override
            public void starting(LifeCycle event) {
    	        HttpFilterInit.init("test");
    	        HttpFilterHelper.addFilter(new HttpFilterEntity(new EchoEndpoint(),new HttpFile("test","gw_jsr.txt")));
                //HttpFilterHelper.addFilter(new HttpFilterEntity(new HttpPostRequestFilter(), 22,new HttpFile("test","gw_post.txt"),new HttpFile("gw_file.txt")));
//              HttpFilterHelper.addFilter(new HttpFilterEntity(new HttpDegradeRequestFilter(), 1,new HttpFile("test","gw_degrade.txt")));
//              HttpFilterHelper.addFilter(new HttpFilterEntity(new HttpDegradeRequestFilter(), 1,new HttpFile("test","gw_degrade.txt")));
              //HttpFilterHelper.addFilter(new HttpFilterEntity(new HttpCCRequestFilter(), 8,new HttpFile("test","gw_cc.txt")));
//              HttpFilterHelper.addFilter(new HttpFilterEntity(new HttpTokenRequestFilter(), 24,new HttpFile("test","gw_token.txt")));
//              HttpFilterHelper.addFilter(new HttpFilterEntity(new HttpTokenResponseFilter(), 0));
    	    }
    	});
    }
}
