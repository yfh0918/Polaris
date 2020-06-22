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
    protected final static Set<Object> SINGLETONS = new LinkedHashSet<Object>();
    protected final static Set<Class<?>> CLASSES = new LinkedHashSet<Class<?>>();

    @Override
    protected void doRegister(Class<?> type, Map<String, Object> attributes, ScannedGenericBeanDefinition beanDefinition) {
        try {
            Class<?> beanClass = Class.forName(beanDefinition.getBeanClassName());
            Component springComponent = AnnotationUtils.findAnnotation(beanClass, Component.class);
            if (springComponent == null) {
                CLASSES.add(beanClass);
            } else {
                Scope springScope = AnnotationUtils.findAnnotation(beanClass, Scope.class);
                if (springScope != null && springScope.value().equals("prototype")) {
                    CLASSES.add(beanClass);
                } else {
                    SINGLETONS.add(springContext.getBean(beanClass));
                }
            }
        } catch (ClassNotFoundException e) {
            throw new ServletContextException(beanDefinition.getBeanClassName() + " is not found");
        }
    }
    
    public Set<Class<?>> getClasses() {
        return CLASSES;
    }

    public Set<Object> getSingletons() {
        return SINGLETONS;
    }
}
