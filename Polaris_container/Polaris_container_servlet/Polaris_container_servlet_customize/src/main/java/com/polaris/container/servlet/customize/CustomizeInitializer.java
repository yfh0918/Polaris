package com.polaris.container.servlet.customize;

import java.util.Set;

import javax.servlet.ServletContext;

import com.polaris.container.servlet.initializer.ServletContextHelper;
import com.polaris.container.servlet.initializer.WebFilterRegister;
import com.polaris.container.servlet.initializer.WebInitParamRegister;
import com.polaris.container.servlet.initializer.WebListenerRegister;
import com.polaris.core.util.SpringUtil;

public class CustomizeInitializer implements javax.servlet.ServletContainerInitializer { 
	
    @Override
    public void onStartup(Set<Class<?>> c, ServletContext servletContext) {
        ServletContextHelper.loadServletContext(servletContext,true);
        new WebInitParamRegister(SpringUtil.getApplicationContext(),servletContext).init();
        new WebListenerRegister(SpringUtil.getApplicationContext(),servletContext).init();
        new WebFilterRegister(SpringUtil.getApplicationContext(),servletContext).init();
        new WebServletRegister(SpringUtil.getApplicationContext(),servletContext).init();
    }
}
