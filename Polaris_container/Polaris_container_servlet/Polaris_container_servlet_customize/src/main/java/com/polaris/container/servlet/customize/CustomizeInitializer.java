package com.polaris.container.servlet.customize;

import java.util.Set;

import javax.servlet.ServletContext;

import com.polaris.container.servlet.initializer.ServletContextHelper;
import com.polaris.core.util.SpringUtil;

public class CustomizeInitializer implements javax.servlet.ServletContainerInitializer { 
	
    @Override
    public void onStartup(Set<Class<?>> c, ServletContext servletContext) {
        ServletContextHelper.loadServletContext(servletContext,true);
        new WebServletRegister(SpringUtil.getApplicationContext(),servletContext).init();
    }
}
