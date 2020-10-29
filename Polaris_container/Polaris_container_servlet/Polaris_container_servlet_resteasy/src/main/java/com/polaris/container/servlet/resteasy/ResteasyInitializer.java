package com.polaris.container.servlet.resteasy;

import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

import org.springframework.context.ConfigurableApplicationContext;

import com.polaris.container.config.ConfigurationHelper;
import com.polaris.container.servlet.initializer.ServletContextHelper;
import com.polaris.container.servlet.initializer.WebComponentFactory;
import com.polaris.container.servlet.initializer.WebComponentFactory.WebComponent;
import com.polaris.core.util.SpringContextHealper;

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
        ConfigurableApplicationContext springContext = SpringContextHealper.createApplicationContext(ConfigurationHelper.getConfiguration());
        springContext.refresh();
        ServletContextHelper.setServletContext(springContext, servletContext);
        WebComponentFactory.init(springContext,servletContext,
                WebComponent.INIT,WebComponent.LISTENER,WebComponent.FILTER);
    }
	

}
