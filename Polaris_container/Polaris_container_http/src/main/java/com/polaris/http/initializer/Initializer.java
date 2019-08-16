package com.polaris.http.initializer;

import java.util.EnumSet;
import java.util.Set;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import com.polaris.core.config.ConfClient;

public class Initializer implements  ServletContainerInitializer { 
	private final String POLARIS_REQUEST_FIRST_FILTER = "PolarisRequestFirstFilter";
	private final String POLARIS_FLOW_CONTROL_FILTER = "PolarisFlowControlFilter";
	private final String POLARIS_SERVLET_TYPE="web.servlet.type";
	private final String POLARIS_WEB_GLOBAL_EXCEPTION="web.global.exception";

	public void onStartup(Set<Class<?>> requestFirstFilters, ServletContext servletContext)  throws ServletException {
		
		// filter
		servletContext.addFilter(POLARIS_REQUEST_FIRST_FILTER, new RequestFirstFilter())
					  .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");
		
		// 流控
		if ("true".equals(ConfClient.get("server.flowcontrol.enabled", "false"))) {
			servletContext.addFilter(POLARIS_FLOW_CONTROL_FILTER, new FlowControlFilter())
			  .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");
		}
		
		//resteasy,提供基本的web.xml配置
		if ("resteasy".equals(ConfClient.get(POLARIS_SERVLET_TYPE, "resteasy"))) {
			
			//init-parameter
			servletContext.setInitParameter("contextConfigLocation", "classpath:META-INF\\spring\\applicationContext.xml");
			String globalException = 
					ConfClient.get(POLARIS_WEB_GLOBAL_EXCEPTION, "com.polaris.http.exception.RestExceptionHandler");
			servletContext.setInitParameter("resteasy.providers", globalException);
			
			//listener
			servletContext.addListener(org.jboss.resteasy.plugins.server.servlet.ResteasyBootstrap.class);
			servletContext.addListener(org.jboss.resteasy.plugins.spring.SpringContextLoaderListener.class);
			
			//servlet
			servletContext.
			    addServlet("dispatcher", "org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher").
			    addMapping("/*");
			
		//springmvc
		} else {
			//init-parameter
			servletContext.setInitParameter("contextConfigLocation", "classpath:META-INF\\spring\\applicationContext.xml");
			servletContext.setInitParameter("spring.profiles.default", "production");
			
			//listener
			servletContext.addListener(org.springframework.web.context.request.RequestContextListener.class);
			servletContext.addListener(org.springframework.web.context.ContextLoaderListener.class);
			
			//filter
			FilterRegistration.Dynamic filter = servletContext.addFilter("encodingFilter", org.springframework.web.filter.CharacterEncodingFilter.class);
			filter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");
			filter.setInitParameter("encoding", "UTF-8");
			filter.setInitParameter("forceEncoding", "true");
			
			//servlet
			servletContext.
			    addServlet("dispatcher", "org.springframework.web.servlet.DispatcherServlet").
			    addMapping("/*");
			
		}
	} 
	

}
