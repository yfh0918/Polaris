package com.polaris.container.servlet.resteasy;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.Application;

import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import com.polaris.core.exception.ServletContextException;

public class ResteasyApplication extends Application {
    protected final static Set<Object> singletons = new LinkedHashSet<Object>();
    protected final static Set<Class<?>> classes = new LinkedHashSet<Class<?>>();

    @Override
    protected void doRegister(Class<?> type, Map<String, Object> attributes, ScannedGenericBeanDefinition beanDefinition) {
        try {
            Class<?> beanClass = Class.forName(beanDefinition.getBeanClassName());
            Component springComponent = AnnotationUtils.findAnnotation(beanClass, Component.class);
            if (springComponent == null) {
                classes.add(beanClass);
            } else {
                Scope springScope = AnnotationUtils.findAnnotation(beanClass, Scope.class);
                if (springScope != null && springScope.value().equals("prototype")) {
                    classes.add(beanClass);
                } else {
                    singletons.add(springContext.getBean(beanClass));
                }
            }
        } catch (ClassNotFoundException e) {
            throw new ServletContextException(beanDefinition.getBeanClassName() + " is not found");
        }
    }
    
    public Set<Class<?>> getClasses() {
        return classes;
    }

    public Set<Object> getSingletons() {
        return singletons;
    }
}
