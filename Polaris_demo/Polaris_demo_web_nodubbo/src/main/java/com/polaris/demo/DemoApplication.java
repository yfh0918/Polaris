package com.polaris.demo;


import javax.servlet.ServletContext;
import javax.websocket.server.ServerContainer;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.polaris.core.Launcher;
import com.polaris.core.config.ConfClient;
import com.polaris.core.naming.NameingClient;
import com.polaris.demo.rest.controller.WsProtocol;
import com.polaris.http.factory.ContainerServerFactory;
import com.polaris.http.listener.ServerListener;
//import com.polaris.demo.configurer.WebConfig;
import com.polaris.http.supports.MainSupport;

/**
 * 入口启动类
 *
 */
@Configuration
@ComponentScan( basePackages={"com.polaris"})
//@ComponentScan( basePackages={"com.polaris"},
//excludeFilters = { @Filter(type=FilterType.ANNOTATION,value=EnableWebMvc.class)}
//)
public class DemoApplication implements Launcher
{
    
    public static void main( String[] args ) throws Exception
    {
//		//启动WEB
//    	MainSupport.startWebServer(args, new Class[]{DemoApplication.class});//springmvc
    	
    	//各类参数载入
    	ConfClient.init();
    	
    	//启动
    	ContainerServerFactory.startServer(new Class[]{DemoApplication.class}, null, new ServerListener() {

			@Override
			public void started(ServletContext servletContext) {
				//注册服务
		    	NameingClient.register();
				
		    	try {
		    		ServerContainer serverContainer = (ServerContainer)servletContext.
	    	    			getAttribute("javax.websocket.server.ServerContainer");
	    	    	serverContainer.addEndpoint(WsProtocol.class);		
				} catch (Exception e) {
					e.printStackTrace();
				} 
			}
			
			@Override
			public void stopped(ServletContext servletContext) {
				//注销服务
		    	NameingClient.unRegister();
			}
    		
    	});
    }
}
