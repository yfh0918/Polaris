package com.polaris.container.servlet.resteasy;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

import org.springframework.core.annotation.Order;

import com.polaris.container.servlet.ServletOrder;
import com.polaris.container.servlet.initializer.ServletContextHelper;
import com.polaris.container.servlet.initializer.WebServletInitializerExtension;

@Order(ServletOrder.RESTEASY)
public class ResteasyInitializer implements WebServletInitializerExtension { 
	
    @Override
    public void onStartup(ServletContext servletContext) {
        servletContext.setInitParameter("javax.ws.rs.core.Application", ResteasyApplication.class.getName());
        servletContext.addListener(org.jboss.resteasy.plugins.server.servlet.ResteasyBootstrap.class);
        ServletRegistration.Dynamic servletRegistration = servletContext.
                addServlet("dispatcher", org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher.class);
                servletRegistration.setLoadOnStartup(1);
                servletRegistration.addMapping("/*");
        ServletContextHelper.loadServletContext(servletContext,true);
    }
	

}
