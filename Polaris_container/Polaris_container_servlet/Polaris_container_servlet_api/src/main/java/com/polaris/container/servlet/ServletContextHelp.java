package com.polaris.container.servlet;

import javax.servlet.ServletContext;

import org.springframework.context.ConfigurableApplicationContext;

import com.polaris.container.config.ConfigurationHelper;
import com.polaris.core.exception.ServletContextException;
import com.polaris.core.util.SpringUtil;

abstract public class ServletContextHelp {
    
    final static String SERVLET_CONTEXT_KEY = "servletContextKey";

    public static void loadServletContext(ServletContext servletContext,boolean refreshSpringContext) {
        ConfigurableApplicationContext context = SpringUtil.createApplicationContext(ConfigurationHelper.getConfiguration());
        loadServletContext(context,servletContext,refreshSpringContext);
    }
    public static void loadServletContext(ConfigurableApplicationContext context, ServletContext servletContext,boolean refreshSpringContext) {
        if (context == null) {
            throw new ServletContextException("loadServletContext is error caused by ConfigurableApplicationContext is null");
        }
        context.getEnvironment().getSystemProperties().put(SERVLET_CONTEXT_KEY, servletContext);
        SpringUtil.setApplicationContext(context);
        if (refreshSpringContext) {
            SpringUtil.refresh();
        } 
    }
    
    public static ServletContext getServletContext(ConfigurableApplicationContext context) {
        if (context == null) {
            return null;
        }
        Object servletContext = context.getEnvironment().getSystemProperties().get(SERVLET_CONTEXT_KEY);
        if (servletContext != null) {
            return (ServletContext)servletContext;
        }
        return null;
    }
}
