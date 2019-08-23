package com.polaris.springmvc;

import javax.servlet.ServletRegistration;

import com.polaris.http.initializer.AbsHttpInitializer;

public class Initializer extends  AbsHttpInitializer { 

	@Override
	public void addInitParameter() {
		servletContext.setInitParameter("contextConfigLocation", "classpath:META-INF\\spring\\applicationContext.xml");
	}

	@Override
	public void addListener() {
		servletContext.addListener(org.springframework.web.context.request.RequestContextListener.class);
		servletContext.addListener(org.springframework.web.context.ContextLoaderListener.class);
	}

	@Override
	public void addFilter() {
	}

	@Override
	public void addServlet() {
		ServletRegistration.Dynamic servletRegistration = servletContext.
	    addServlet("dispatcher", org.springframework.web.servlet.DispatcherServlet.class);
	    servletRegistration.setInitParameter("contextConfigLocation", "classpath:META-INF\\spring\\spring-context-mvc.xml");
	    servletRegistration.setLoadOnStartup(1);
	    servletRegistration.addMapping("/*");	
	} 
	

}
