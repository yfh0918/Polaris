package com.polaris.demo;


import javax.servlet.ServletContext;

import com.polaris.core.Launcher;
import com.polaris.core.annotation.PolarisApplication;
import com.polaris.core.config.ConfClient;
import com.polaris.core.naming.NameingClient;
import com.polaris.http.factory.ContainerServerFactory;
import com.polaris.http.listener.ServerListener;

/**
 * 入口启动类
 *
 */
@PolarisApplication(scanBasePackages={"com.polaris"})
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
			}
			
			@Override
			public void stopped(ServletContext servletContext) {
				//注销服务
		    	NameingClient.unRegister();
			}
    		
    	});
    }
}
