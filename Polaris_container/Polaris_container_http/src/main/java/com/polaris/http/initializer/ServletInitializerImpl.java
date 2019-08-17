package com.polaris.http.initializer;

import java.util.EnumSet;
import java.util.ServiceLoader;
import java.util.Set;

import javax.servlet.DispatcherType;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import com.polaris.core.config.ConfClient;

public class ServletInitializerImpl implements  ServletContainerInitializer { 
	private final String POLARIS_REQUEST_FIRST_FILTER = "PolarisRequestFirstFilter";
	private final String POLARIS_FLOW_CONTROL_FILTER = "PolarisFlowControlFilter";
	
	private final ServiceLoader<HttpInitializer> serviceLoader = ServiceLoader.load(HttpInitializer.class);


	public void onStartup(Set<Class<?>> requestFirstFilters, ServletContext servletContext)  throws ServletException {
		
		//contex为空直接返回
		if (servletContext == null) {
			return;
		}
		
		// filter
		servletContext.addFilter(POLARIS_REQUEST_FIRST_FILTER, new RequestFirstFilter())
					  .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");
		
		// 流控
		if ("true".equals(ConfClient.get("server.flowcontrol.enabled", "false"))) {
			servletContext.addFilter(POLARIS_FLOW_CONTROL_FILTER, new FlowControlFilter())
			  .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");
		}
		
		//加载外部扩展的filter,Listener,servlet
		for (HttpInitializer httpInitializer : serviceLoader) {
			httpInitializer.onStartup(servletContext);
		}
	} 
	

}
