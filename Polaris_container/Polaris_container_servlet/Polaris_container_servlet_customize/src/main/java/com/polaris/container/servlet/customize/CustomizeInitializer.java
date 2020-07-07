package com.polaris.container.servlet.customize;

import java.util.Set;

import javax.servlet.ServletContext;

import com.polaris.container.servlet.initializer.ServletContextHelper;
import com.polaris.container.servlet.initializer.WebComponentFactory;
import com.polaris.container.servlet.initializer.WebComponentFactory.WebComponent;

public class CustomizeInitializer implements javax.servlet.ServletContainerInitializer { 
	
    @Override
    public void onStartup(Set<Class<?>> c, ServletContext servletContext) {
        ServletContextHelper.loadServletContext(servletContext,true);
        WebComponentFactory.init(WebComponent.INIT,WebComponent.LISTENER,WebComponent.FILTER,WebComponent.SERVLET);
    }
}
