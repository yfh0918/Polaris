package com.polaris.resteasy;

import javax.servlet.ServletRegistration;

import com.polaris.core.util.SpringUtil;
import com.polaris.http.initializer.AbsHttpInitializer;

public class Initializer extends  AbsHttpInitializer { 

	@Override
	public void loadContext() {
		SpringUtil.refresh();
	} 
	
	@Override
	public void addInitParameter() {
        servletContext.setInitParameter("javax.ws.rs.core.Application", "com.polaris.resteasy.ResteasyApplication");
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
