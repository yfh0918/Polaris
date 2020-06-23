package com.polaris.container.servlet.initializer;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;

import com.polaris.container.config.ConfigurationHelper;
import com.polaris.core.component.Initial;
import com.polaris.core.util.Requires;

public abstract class ComponentScanRegister implements Initial{
    protected ConfigurableApplicationContext springContext;
    protected ServletContext servletContext;
    protected Class<?>[] types;
    private Set<ScannedGenericBeanDefinition> candidateComponents = new HashSet<>();

    public ComponentScanRegister() {
    }
    
    public ComponentScanRegister(ConfigurableApplicationContext springContext, ServletContext servletContext, Class<?>... types) {
        this.springContext = springContext;
        this.servletContext = servletContext;
        this.types = types;
    }
    
    @Override
    public void init() {
        findCandidateComponents(getTypeFilters(), getCandidateComponents());
        registerCandidateComponents(getCandidateComponents());
    }
    public Set<ScannedGenericBeanDefinition> getCandidateComponents() {
        return candidateComponents;
    }
    @SuppressWarnings("unchecked")
    public List<TypeFilter> getTypeFilters() {
        List<TypeFilter> typeFilters = new ArrayList<>();
        for (Class<?> type : types) {
            if (Annotation.class.isAssignableFrom(type)) {
                typeFilters.add(new AnnotationTypeFilter((Class<? extends Annotation>)type));
            } else {
                typeFilters.add(new AssignableTypeFilter(type));
            }
        }
        return typeFilters;
    } 
    
    protected void findCandidateComponents(List<TypeFilter> typeFilters, Set<ScannedGenericBeanDefinition> candidateComponents) {
        if (candidateComponents != null && candidateComponents.size() > 0) {
            return;
        }
        requires();
        ClassPathScanningCandidateComponentProvider componentProvider = new ClassPathScanningCandidateComponentProvider(
                false);
        componentProvider.setEnvironment(this.springContext.getEnvironment());
        componentProvider.setResourceLoader(this.springContext);
        for (TypeFilter typeFilter : typeFilters) {
            componentProvider.addIncludeFilter(typeFilter);
        }
        List<String> basePackageList = ConfigurationHelper.getBasePackageList();
        for (String basePackage : basePackageList) {
            for (BeanDefinition candidate : componentProvider.findCandidateComponents(basePackage)) {
                if (candidate instanceof ScannedGenericBeanDefinition) {
                    candidateComponents.add((ScannedGenericBeanDefinition)candidate);
                }
            }
        }
    }
    
    protected void registerCandidateComponents(Set<ScannedGenericBeanDefinition> candidateComponents) {
        requires();
        for (Class<?> type : types) {
            for (ScannedGenericBeanDefinition candidate : candidateComponents) {
                if (Annotation.class.isAssignableFrom(type)) {
                    Map<String, Object> attributes = ((ScannedGenericBeanDefinition)candidate).getMetadata()
                            .getAnnotationAttributes(type.getName());
                    if (attributes != null) {
                        doRegister(type, attributes,(ScannedGenericBeanDefinition)candidate);
                    }
                } else {
                    if (type.getName().equals(candidate.getBeanClassName())) {
                        doRegister(type, null,(ScannedGenericBeanDefinition)candidate);
                    }
                }
            }
        }
    }
    
    abstract protected void doRegister(Class<?> type, Map<String, Object> attributes,  ScannedGenericBeanDefinition beanDefinition);
    
    protected void requires() {
        Requires.requireNonNull(springContext,"ConfigurableApplicationContext is null");
        Requires.requireNonNull(servletContext,"ServletContext is null");
    }
}
