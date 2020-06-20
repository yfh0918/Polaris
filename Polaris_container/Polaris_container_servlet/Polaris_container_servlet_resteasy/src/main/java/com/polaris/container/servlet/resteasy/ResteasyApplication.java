package com.polaris.container.servlet.resteasy;

import java.util.Map;

import javax.ws.rs.Path;
import javax.ws.rs.core.Application;

import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import com.polaris.core.exception.ServletContextException;
import com.polaris.core.util.SpringUtil;

public class ResteasyApplication extends Application {
    
    @Override
    protected void doRegister(Map<String, Object> attributes, ScannedGenericBeanDefinition beanDefinition) {
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
                    singletons.add(SpringUtil.getApplicationContext().getBean(beanClass));
                }
            }
        } catch (ClassNotFoundException e) {
            throw new ServletContextException(beanDefinition.getBeanClassName() + " is not found");
        }
    }
    
    public static void registerEndPoint(Object obj) {
    	Path path = obj.getClass().getAnnotation(Path.class);
    	if (path != null) {
        	singletons.add(obj);
    	}
    }
    public static void registerEndPoint(Class<?> clazz) {
    	Path path = clazz.getAnnotation(Path.class);
    	if (path != null) {
        	classes.add(clazz);
    	}
    }
}
