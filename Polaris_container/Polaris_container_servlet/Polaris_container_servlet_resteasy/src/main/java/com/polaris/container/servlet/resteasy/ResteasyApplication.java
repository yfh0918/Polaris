package com.polaris.container.servlet.resteasy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Path;
import javax.ws.rs.core.Application;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;

import com.polaris.container.config.ConfigurationHelper;
import com.polaris.core.util.SpringUtil;

public class ResteasyApplication extends Application {
    
	private static Set<Object> singletons = new LinkedHashSet<Object>();
    private static Set<Class<?>> classes = new LinkedHashSet<Class<?>>();
 
    private static final List<AnnotationTypeFilter> TYPE_FILTERS;
    private static Set<ScannedGenericBeanDefinition> candidateComponents = new HashSet<>();

    static {
        List<AnnotationTypeFilter> servletComponentTypeFilters = new ArrayList<>();
        servletComponentTypeFilters.add(new AnnotationTypeFilter(Path.class));
        TYPE_FILTERS = Collections.unmodifiableList(servletComponentTypeFilters);
    }
    
    public ResteasyApplication() {
        findCandidateComponents();
        registerCandidateComponents();
    }
 
    @Override
    public Set<Class<?>> getClasses() {
        return classes;
    }
 
    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }
    
    private void findCandidateComponents() {
        if (candidateComponents.size() > 0) {
            return;
        }
        ClassPathScanningCandidateComponentProvider componentProvider = new ClassPathScanningCandidateComponentProvider(
                false);
        componentProvider.setEnvironment(SpringUtil.getApplicationContext().getEnvironment());
        componentProvider.setResourceLoader(SpringUtil.getApplicationContext());
        for (AnnotationTypeFilter typeFilter : TYPE_FILTERS) {
            componentProvider.addIncludeFilter(typeFilter);
        }
        Class<?>[] clazz = ConfigurationHelper.getClasses();
        for (int i0 = 0; i0 < clazz.length; i0++) {
            for (BeanDefinition candidate : componentProvider
                    .findCandidateComponents(clazz[i0].getPackage().getName())) {
                if (candidate instanceof ScannedGenericBeanDefinition) {
                    candidateComponents.add((ScannedGenericBeanDefinition)candidate);
                }
            }
        }
    }
    private void registerCandidateComponents() {
        for (ScannedGenericBeanDefinition candidate : candidateComponents) {
            Map<String, Object> attributes = ((ScannedGenericBeanDefinition)candidate).getMetadata()
                    .getAnnotationAttributes(Path.class.getName());
            if (attributes != null) {
                try {
                    Class<?> beanClass = Class.forName(candidate.getBeanClassName());
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
                    continue;
                }
            }
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
