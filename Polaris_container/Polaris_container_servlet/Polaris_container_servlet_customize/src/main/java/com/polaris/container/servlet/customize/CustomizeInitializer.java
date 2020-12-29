package com.polaris.container.servlet.customize;

import java.util.Set;

import javax.servlet.ServletContext;

import org.springframework.context.ConfigurableApplicationContext;

import com.polaris.container.config.ConfigurationProxy;
import com.polaris.container.servlet.initializer.ServletContextHelper;
import com.polaris.container.servlet.initializer.WebComponentFactory;
import com.polaris.container.servlet.initializer.WebComponentFactory.WebComponent;
import com.polaris.core.util.SpringContextHealper;

public class CustomizeInitializer implements javax.servlet.ServletContainerInitializer { 
	
    @Override
    public void onStartup(Set<Class<?>> c, ServletContext servletContext) {
        ConfigurableApplicationContext springContext = SpringContextHealper.createApplicationContext(ConfigurationProxy.INSTANCE.getConfiguration());
        ServletContextHelper.setServletContext(springContext, servletContext);
        springContext.refresh();
        WebComponentFactory.init(springContext,servletContext,
                WebComponent.INIT,WebComponent.LISTENER,WebComponent.FILTER,WebComponent.SERVLET);
    }
}
