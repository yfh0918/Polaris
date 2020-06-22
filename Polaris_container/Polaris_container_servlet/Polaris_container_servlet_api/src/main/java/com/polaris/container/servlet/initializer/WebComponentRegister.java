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
import org.springframework.core.type.filter.TypeFilter;

public abstract class WebComponentRegister extends ComponentScanRegister{

    private static final List<TypeFilter> TYPE_FILTERS;
    private static Set<ScannedGenericBeanDefinition> CANDIDATE_COMPONENTS = new HashSet<>();

    static {
        List<TypeFilter> servletComponentTypeFilters = new ArrayList<>();
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
    public List<TypeFilter> getTypeFilters() {
        return TYPE_FILTERS;
    }
    
    public Set<ScannedGenericBeanDefinition> getCandidateComponents() {
        return CANDIDATE_COMPONENTS;
    }
}
