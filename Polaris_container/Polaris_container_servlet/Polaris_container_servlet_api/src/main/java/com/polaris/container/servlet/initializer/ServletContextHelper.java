package com.polaris.container.servlet.initializer;

import javax.servlet.ServletContext;

import org.springframework.context.ConfigurableApplicationContext;

import com.polaris.core.exception.ServletContextException;

/**
* ServletContext helper class
* 
*/
abstract public class ServletContextHelper {
    
    final static String SERVLET_CONTEXT_KEY = "PolarisServletContextKey";

    /**
    * load servlet context into spring context
    * 
    */
    public static void setServletContext(ConfigurableApplicationContext context, ServletContext servletContext) {
        if (context == null) {
            throw new ServletContextException("loadServletContext is error caused by ConfigurableApplicationContext is null");
        }
        context.getEnvironment().getSystemProperties().put(SERVLET_CONTEXT_KEY, servletContext);
    }
    
    /**
     * get servlet context from spring context
     * 
     */
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
