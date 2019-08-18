package com.polaris.resteasy;

import javax.servlet.ServletRegistration;

import com.polaris.core.config.ConfClient;
import com.polaris.http.initializer.AbsHttpInitializer;

public class Initializer extends  AbsHttpInitializer { 
	private final String POLARIS_WEB_GLOBAL_EXCEPTION="web.global.exception";


	@Override
	public void addInitParameter() {
		servletContext.setInitParameter("contextConfigLocation", "classpath:META-INF\\spring\\applicationContext.xml");
		String globalException = 
				ConfClient.get(POLARIS_WEB_GLOBAL_EXCEPTION, "com.polaris.resteasy.RestExceptionHandler");
		servletContext.setInitParameter("resteasy.providers", globalException);

		
	}

	@Override
	public void addListener() {
		servletContext.addListener(org.jboss.resteasy.plugins.server.servlet.ResteasyBootstrap.class);
		servletContext.addListener(org.jboss.resteasy.plugins.spring.SpringContextLoaderListener.class);
	}

	@Override
	public void addFilter() {
	}

	@Override
	public void addServlet() {
		ServletRegistration.Dynamic servletRegistration = servletContext.
			    addServlet("dispatcher", org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher.class);
			    servletRegistration.setInitParameter("contextConfigLocation", "classpath*:/spring-context-mvc.xml");
			    servletRegistration.setLoadOnStartup(1);
			    servletRegistration.addMapping("/*");	
	} 
}
