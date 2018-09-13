package com.polaris.http.filter;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.DispatcherType;
import javax.servlet.Servlet;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;

import org.jboss.resteasy.plugins.server.servlet.ResteasyBootstrap;
import org.jboss.resteasy.plugins.spring.SpringContextLoaderListener;

public class RequestFirstFilterInitializer implements  ServletContainerInitializer { 
	private final String REQUEST_FIRST_FILTER = "RequestFirstFilter";
	private final static Map<String, String> initParameterMap = new HashMap<>();
	private final static List<ServletContextListener> listenerList = new ArrayList<>();
	private final static Map<String, Class <? extends Servlet>> servletClassMap = new HashMap<>();
	private final static Map<String, String[]> servletUrlMap = new HashMap<>();
	
	static {
		//initParamete
		initParameterMap.put("resteasy.providers", "com.polaris.http.exception.RestExceptionHandler");
		initParameterMap.put("contextConfigLocation", "classpath:META-INF\\spring\\applicationContext.xml");
		
		//listener
		listenerList.add(new ResteasyBootstrap());
		listenerList.add(new SpringContextLoaderListener());
		
		//servlet
		String[] urls = {"/api/*","/rest/*"};
		servletClassMap.put("Resteasy", org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher.class);
		servletUrlMap.put("Resteasy", urls);
	}
	
	// jetty存在bug只能通过外部接口单独提供Listener
	public static List<ServletContextListener> getListenerList() {
		return listenerList;
	}

	// listener目前只支持tomcat
	public void onStartup(Set<Class<?>> requestFirstFilters, ServletContext servletContext)  throws ServletException {
		
		// filter
		servletContext.addFilter(REQUEST_FIRST_FILTER, new RequestFirstFilter())
					  .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");

		//iniParameter
        for (Entry<String, String> entry : initParameterMap.entrySet()) {
        	servletContext.setInitParameter(entry.getKey(), entry.getValue());
        }

        //servlet
        for (Entry<String, Class <? extends Servlet>> entry : servletClassMap.entrySet()) {
        	servletContext.addServlet(entry.getKey(), entry.getValue())
        				  .addMapping(servletUrlMap.get(entry.getKey()));
        }

        //Listener
        for (ServletContextListener listerClass : listenerList) {
        	servletContext.addListener(listerClass);
        }
	} 
	

}
