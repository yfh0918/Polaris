package com.polaris.container.servlet.resteasy;

import javax.servlet.ServletRegistration;

import org.springframework.core.annotation.Order;

import com.polaris.container.servlet.ServletContextHelper;
import com.polaris.container.servlet.ServletOrder;
import com.polaris.container.servlet.initializer.ExtensionInitializerAbs;

@Order(ServletOrder.RESTEASY)
public class ResteasyInitializer extends  ExtensionInitializerAbs { 
	
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
		ServletContextHelper.loadServletContext(servletContext,true);
	}
	

}
