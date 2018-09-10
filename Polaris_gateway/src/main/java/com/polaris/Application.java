package com.polaris;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.polaris.comm.config.ConfClient;
import com.polaris.comm.util.SpringUtil;
import com.polaris.gateway.support.ApplicationSupport;

public class Application {
    @SuppressWarnings("resource")
	public static void main(String[] args) {
    	
    	//载入spring
    	ConfClient.setAppName("Polaris_gateway");
    	new ClassPathXmlApplicationContext(SpringUtil.SPRING_PATH);
    	
    	//设置日志
    	ApplicationSupport.configureAndWatch(60000);
    	
    	//启动网关应用
    	ApplicationSupport.startGateway();
    }
}
