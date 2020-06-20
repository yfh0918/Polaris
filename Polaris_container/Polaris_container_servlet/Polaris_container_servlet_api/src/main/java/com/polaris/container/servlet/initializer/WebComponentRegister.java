package com.polaris.container.servlet.initializer;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebListener;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import com.polaris.container.config.ConfigurationHelper;
import com.polaris.core.component.Initial;
import com.polaris.core.util.Requires;

public abstract class WebComponentRegister implements Initial{

    protected ConfigurableApplicationContext springContext;
    protected ServletContext servletContext;
    protected Class<? extends Annotation> annotationType;

    private static final List<AnnotationTypeFilter> TYPE_FILTERS;
    private static ClassPathScanningCandidateComponentProvider componentProvider;

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
        Requires.requireNonNull(springContext,"ConfigurableApplicationContext is null");
        Requires.requireNonNull(servletContext,"ServletContext is null");
        Requires.requireNonNull(annotationType,"AnnotationType is null");
        this.springContext = springContext;
        this.servletContext = servletContext;
        this.annotationType = annotationType;
    }
    
    @Override
    public void init() {
        Class<?>[] clazz = ConfigurationHelper.getClasses();
        for (int i0 = 0; i0 < clazz.length; i0++) {
            scanPackage(clazz[i0].getPackage().getName());
        }
    }
    private void createComponentProvider() {
        if (componentProvider != null) {
            return;
        }
        componentProvider = new ClassPathScanningCandidateComponentProvider(
                false);
        componentProvider.setEnvironment(this.springContext.getEnvironment());
        componentProvider.setResourceLoader(this.springContext);
        for (AnnotationTypeFilter typeFilter : TYPE_FILTERS) {
            componentProvider.addIncludeFilter(typeFilter);
        }
    }
    
    private void scanPackage(String packageToScan) {
        createComponentProvider();
        for (BeanDefinition candidate : componentProvider
                .findCandidateComponents(packageToScan)) {
            if (candidate instanceof ScannedGenericBeanDefinition) {
                
                Map<String, Object> attributes = ((ScannedGenericBeanDefinition)candidate).getMetadata()
                        .getAnnotationAttributes(annotationType.getName());
                if (attributes != null) {
                    doRegister(attributes,(ScannedGenericBeanDefinition)candidate);
                }
                
            }
        }
    }
    
    abstract protected void doRegister(Map<String, Object> attributes, ScannedGenericBeanDefinition beanDefinition);
}
