package com.polaris.container.servlet.resteasy;

import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

import com.polaris.container.servlet.initializer.ServletContextHelper;
import com.polaris.container.servlet.initializer.WebFilterRegister;
import com.polaris.container.servlet.initializer.WebInitParamRegister;
import com.polaris.container.servlet.initializer.WebListenerRegister;
import com.polaris.core.util.SpringUtil;

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
        new WebInitParamRegister(SpringUtil.getApplicationContext(),servletContext).init();
        new WebListenerRegister(SpringUtil.getApplicationContext(),servletContext).init();
        new WebFilterRegister(SpringUtil.getApplicationContext(),servletContext).init();

    }
	

}
