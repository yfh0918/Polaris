package com.polaris.resteasy;

import javax.servlet.ServletRegistration;

import com.polaris.core.util.SpringUtil;
import com.polaris.server.initializer.ExtensionInitializerAbs;

public class ResteasyInitializer extends  ExtensionInitializerAbs { 

	@Override
	public void loadContext() {
		SpringUtil.refresh();
	} 
	
	@Override
	public void addInitParameter() {
        servletContext.setInitParameter("javax.ws.rs.core.Application", ResteasyApplication.class.getName());
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
