package com.polaris.container.servlet.initializer;

import javax.servlet.ServletContext;

import org.springframework.context.ConfigurableApplicationContext;

import com.polaris.container.config.ConfigurationHelper;
import com.polaris.core.exception.ServletContextException;
import com.polaris.core.util.SpringUtil;

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
        new WebInitParamRegister(context,servletContext).init();
        new WebListenerRegister(context,servletContext).init();
        new WebFilterRegister(context,servletContext).init();
    }
    
    /**
     * get servlet context from spring context
     * 
     */
    public static ServletContext getServletContext() {
        return getServletContext(SpringUtil.getApplicationContext());
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
