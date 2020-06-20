package com.polaris.container.servlet.resteasy;

import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

import com.polaris.container.servlet.initializer.ServletContextHelper;

public class ResteasyInitializer implements javax.servlet.ServletContainerInitializer { 
	
    @Override
    public void onStartup(Set<Class<?>> c, ServletContext servletContext) {
        servletContext.setInitParameter("javax.ws.rs.core.Application", ResteasyApplication.class.getName());
        servletContext.addListener(org.jboss.resteasy.plugins.server.servlet.ResteasyBootstrap.class);
        ServletRegistration.Dynamic servletRegistration = servletContext.
                addServlet("dispatcher", org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher.class);
                servletRegistration.setLoadOnStartup(1);
                servletRegistration.addMapping("/*");
        ServletContextHelper.loadServletContext(servletContext,true);
    }
	

}
