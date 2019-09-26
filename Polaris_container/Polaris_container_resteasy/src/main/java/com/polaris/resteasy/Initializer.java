package com.polaris.resteasy;

import javax.servlet.ServletRegistration;

import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.polaris.http.initializer.AbsHttpInitializer;
import com.polaris.http.initializer.WebConfigInitializer;

public class Initializer extends  AbsHttpInitializer { 
	private AnnotationConfigApplicationContext applicationContext = null;

	@Override
	public void loadContext() {
		applicationContext = new AnnotationConfigApplicationContext();
		applicationContext.register(WebConfigInitializer.getRootConfigs());
		applicationContext.refresh();
	} 
	
	@Override
	public void addInitParameter() {
        servletContext.setInitParameter("javax.ws.rs.core.Application", "com.polaris.resteasy.ResteasyApplication");
        servletContext.setInitParameter(ResteasyContextParameters.RESTEASY_PROVIDERS, "com.polaris.resteasy.RestExceptionHandler");
        super.addInitParameter();
	}

	@Override
	public void addListener() {
		servletContext.addListener(org.jboss.resteasy.plugins.server.servlet.ResteasyBootstrap.class);
		super.addListener();
		
	}

	@Override
	public void addFilter() {
		super.addFilter();
	}

	@Override
	public void addServlet() {
		ServletRegistration.Dynamic servletRegistration = servletContext.
			    addServlet("dispatcher", org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher.class);
			    servletRegistration.setLoadOnStartup(1);
			    servletRegistration.addMapping("/*");	
	}

}
