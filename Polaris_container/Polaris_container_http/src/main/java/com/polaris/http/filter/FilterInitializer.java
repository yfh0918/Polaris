package com.polaris.http.filter;

import java.util.EnumSet;
import java.util.Set;

import javax.servlet.DispatcherType;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import com.polaris.core.config.ConfClient;

public class FilterInitializer implements  ServletContainerInitializer { 
	private final String POLARIS_REQUEST_FIRST_FILTER = "PolarisRequestFirstFilter";
	private final String POLARIS_FLOW_CONTROL_FILTER = "PolarisFlowControlFilter";
//	private final static Map<String, String> initParameterMap = new HashMap<>();
//	private final static List<ServletContextListener> listenerList = new ArrayList<>();
//	private final static Map<String, Class <? extends Servlet>> servletClassMap = new HashMap<>();
//	private final static Map<String, String[]> servletUrlMap = new HashMap<>();
//	
//	static {
//		//initParamete
//		initParameterMap.put("resteasy.providers", "com.polaris.http.exception.RestExceptionHandler");
//		initParameterMap.put("contextConfigLocation", "classpath:META-INF\\spring\\applicationContext.xml");
//		
//		//listener
//		listenerList.add(new ResteasyBootstrap());
//		listenerList.add(new SpringContextLoaderListener());
//		
//		//servlet
//		String[] urls = {"/api/*","/rest/*"};
//		servletClassMap.put("Resteasy", org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher.class);
//		servletUrlMap.put("Resteasy", urls);
//	}

	// listener目前只支持tomcat
	public void onStartup(Set<Class<?>> requestFirstFilters, ServletContext servletContext)  throws ServletException {
		
		// filter
		servletContext.addFilter(POLARIS_REQUEST_FIRST_FILTER, new RequestFirstFilter())
					  .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");
		
		// 流控
		if ("true".equals(ConfClient.get("server.flowcontrol.enabled", "false"))) {
			servletContext.addFilter(POLARIS_FLOW_CONTROL_FILTER, new FlowControlFilter())
			  .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");
		}

	} 
	

}
