package com.polaris.container.servlet.initializer;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebListener;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.type.filter.AnnotationTypeFilter;

public abstract class WebComponentRegister extends ComponentScanRegister{

    private static final List<AnnotationTypeFilter> TYPE_FILTERS;
    private static Set<ScannedGenericBeanDefinition> candidateComponents = new HashSet<>();

    static {
        List<AnnotationTypeFilter> servletComponentTypeFilters = new ArrayList<>();
        servletComponentTypeFilters.add(new AnnotationTypeFilter(WebListener.class));
        servletComponentTypeFilters.add(new AnnotationTypeFilter(WebFilter.class));
        servletComponentTypeFilters.add(new AnnotationTypeFilter(WebInitParam.class));
        TYPE_FILTERS = Collections.unmodifiableList(servletComponentTypeFilters);
    }
    
    public static void loadWebComponent(ConfigurableApplicationContext springContext, ServletContext servletContext) {
        new WebInitParamRegister(springContext,servletContext).init();
        new WebListenerRegister(springContext,servletContext).init();
        new WebFilterRegister(springContext,servletContext).init();
    }
    
    public WebComponentRegister(ConfigurableApplicationContext springContext, ServletContext servletContext, Class<? extends Annotation> annotationType) {
        super(springContext,servletContext,annotationType);
    }
    
    @Override
    public void init() {
        if (candidateComponents.size() == 0) {
            candidateComponents = findCandidateComponents(TYPE_FILTERS);
        }
        registerCandidateComponents(candidateComponents);
    }
}
