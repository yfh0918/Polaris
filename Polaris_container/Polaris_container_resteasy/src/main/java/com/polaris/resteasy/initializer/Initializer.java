package com.polaris.resteasy.initializer;

import com.polaris.core.config.ConfClient;
import com.polaris.http.initializer.AbsHttpInitializer;

public class Initializer extends  AbsHttpInitializer { 
	private final String POLARIS_WEB_GLOBAL_EXCEPTION="web.global.exception";


	@Override
	public void addInitParameter() {
		servletContext.setInitParameter("contextConfigLocation", "classpath:META-INF\\spring\\applicationContext.xml");
		String globalException = 
				ConfClient.get(POLARIS_WEB_GLOBAL_EXCEPTION, "com.polaris.resteasy.exception.RestExceptionHandler");
		servletContext.setInitParameter("resteasy.providers", globalException);

		
	}

	@Override
	public void addListener() {
		servletContext.addListener(org.jboss.resteasy.plugins.server.servlet.ResteasyBootstrap.class);
		servletContext.addListener(org.jboss.resteasy.plugins.spring.SpringContextLoaderListener.class);
	}

	@Override
	public void addFilter() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addServlet() {
		servletContext.
	    addServlet("dispatcher", "org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher").
	    addMapping("/*");
	} 
	

}
