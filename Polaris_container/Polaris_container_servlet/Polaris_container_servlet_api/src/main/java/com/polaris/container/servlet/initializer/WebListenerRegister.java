package com.polaris.container.servlet.initializer;

import java.util.EventListener;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.annotation.WebListener;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;

import com.polaris.core.exception.ServletContextException;

public class WebListenerRegister extends WebComponentRegister{
    private static final Set<Class <? extends EventListener>> webListenerSet = new LinkedHashSet<>();

    public WebListenerRegister(ConfigurableApplicationContext springContext, ServletContext servletContext) {
        super(springContext,servletContext,WebListener.class);
    }
    
    public static void register(Class <? extends EventListener> listenerClass) {
        webListenerSet.add(listenerClass);
    }
    @Override
    public void init() {
        super.init();
        addListenerToServletContext();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void doRegister(Class<?> type, Map<String, Object> attributes, ScannedGenericBeanDefinition beanDefinition) {
        try {
            webListenerSet.add((Class <? extends EventListener>)(Class.forName(beanDefinition.getBeanClassName())));
        } catch (ClassNotFoundException e) {
            throw new ServletContextException(beanDefinition.getBeanClassName() + " is not found");
        }
    }
    
    private void addListenerToServletContext() {
        for (Class <? extends EventListener> listenerClass : webListenerSet) {
            servletContext.addListener(listenerClass);
        }
    }
}
