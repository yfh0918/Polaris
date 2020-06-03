package com.polaris.container.servlet;

import javax.servlet.ServletContext;

import org.springframework.context.ConfigurableApplicationContext;

import com.polaris.core.util.SpringUtil;

abstract public class ServletContextHelp {
    
    final static String SERVLET_CONTEXT_KEY = "servletContextKey";

    public static void loadServletContext(ConfigurableApplicationContext context, ServletContext servletContext,boolean refreshSpringContext) {
        context.getEnvironment().getSystemProperties().put(SERVLET_CONTEXT_KEY, servletContext);
        if (refreshSpringContext) {
            SpringUtil.refresh();
        } 
        SpringUtil.setApplicationContext(context);
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
