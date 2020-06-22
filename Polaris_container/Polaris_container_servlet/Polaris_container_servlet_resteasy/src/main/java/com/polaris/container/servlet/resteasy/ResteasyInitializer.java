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
	
    private static final String REST_APPLICATION = "javax.ws.rs.core.Application";
    private static final String REST_SERVLET = "dispatcher";
    private static final String MAPPING_ALL = "/*";
    
    @Override
    public void onStartup(Set<Class<?>> c, ServletContext servletContext) {
        servletContext.setInitParameter(REST_APPLICATION, ResteasyApplication.class.getName());
        servletContext.addListener(org.jboss.resteasy.plugins.server.servlet.ResteasyBootstrap.class);
        ServletRegistration.Dynamic servletRegistration = servletContext.
                addServlet(REST_SERVLET, org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher.class);
                servletRegistration.setLoadOnStartup(1);
                servletRegistration.addMapping(MAPPING_ALL);
        ServletContextHelper.loadServletContext(servletContext,true);
        new WebInitParamRegister(SpringUtil.getApplicationContext(),servletContext).init();
        new WebListenerRegister(SpringUtil.getApplicationContext(),servletContext).init();
        new WebFilterRegister(SpringUtil.getApplicationContext(),servletContext).init();
    }
	

}
