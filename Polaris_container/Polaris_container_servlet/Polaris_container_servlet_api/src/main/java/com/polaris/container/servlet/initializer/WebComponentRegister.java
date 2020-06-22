package com.polaris.container.servlet.initializer;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebListener;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

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
    
    public WebComponentRegister(ConfigurableApplicationContext springContext, ServletContext servletContext, Class<? extends Annotation> annotationType) {
        super(springContext,servletContext,annotationType);
    }
    
    protected String determineName(Map<String, Object> attributes,
            BeanDefinition beanDefinition, String key) {
        return (String) (StringUtils.hasText((String) attributes.get(key))
                ? attributes.get(key) : beanDefinition.getBeanClassName());
    }
    
    protected Map<String, String> extractInitParameters(
            Map<String, Object> attributes) {
        Map<String, String> initParameters = new HashMap<>();
        for (AnnotationAttributes initParam : (AnnotationAttributes[]) attributes
                .get("initParams")) {
            String name = (String) initParam.get("name");
            String value = (String) initParam.get("value");
            initParameters.put(name, value);
        }
        return initParameters;
    }
    
    protected EnumSet<DispatcherType> extractDispatcherTypes(
            Map<String, Object> attributes) {
        DispatcherType[] dispatcherTypes = (DispatcherType[]) attributes
                .get("dispatcherTypes");
        if (dispatcherTypes.length == 0) {
            return EnumSet.noneOf(DispatcherType.class);
        }
        if (dispatcherTypes.length == 1) {
            return EnumSet.of(dispatcherTypes[0]);
        }
        return EnumSet.of(dispatcherTypes[0],
                Arrays.copyOfRange(dispatcherTypes, 1, dispatcherTypes.length));
    }
    
    protected String[] extractUrlPatterns(Map<String, Object> attributes) {
        String[] value = (String[]) attributes.get("value");
        String[] urlPatterns = (String[]) attributes.get("urlPatterns");
        if (urlPatterns.length > 0) {
            Assert.state(value.length == 0,
                    "The urlPatterns and value attributes are mutually exclusive.");
            return urlPatterns;
        }
        return value;
    }
    
    @Override
    public List<TypeFilter> getTypeFilters() {
        return TYPE_FILTERS;
    }
    
    @Override
    public Set<ScannedGenericBeanDefinition> getCandidateComponents() {
        return CANDIDATE_COMPONENTS;
    }
}
