package com.polaris.container.servlet.initializer;

import java.lang.annotation.Annotation;
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

import com.polaris.container.config.ConfigurationHelper;
import com.polaris.core.component.Initial;
import com.polaris.core.util.Requires;

public abstract class ComponentScanRegister implements Initial{

    protected ConfigurableApplicationContext springContext;
    protected ServletContext servletContext;
    protected Class<? extends Annotation> annotationType;

    public ComponentScanRegister(ConfigurableApplicationContext springContext, ServletContext servletContext, Class<? extends Annotation> annotationType) {
        Requires.requireNonNull(springContext,"ConfigurableApplicationContext is null");
        Requires.requireNonNull(servletContext,"ServletContext is null");
        Requires.requireNonNull(annotationType,"AnnotationType is null");
        this.springContext = springContext;
        this.servletContext = servletContext;
        this.annotationType = annotationType;
    }
    
    protected Set<ScannedGenericBeanDefinition> findCandidateComponents(List<AnnotationTypeFilter> typeFilters) {
        Set<ScannedGenericBeanDefinition> candidateComponents = new HashSet<>();
        ClassPathScanningCandidateComponentProvider componentProvider = new ClassPathScanningCandidateComponentProvider(
                false);
        componentProvider.setEnvironment(this.springContext.getEnvironment());
        componentProvider.setResourceLoader(this.springContext);
        for (AnnotationTypeFilter typeFilter : typeFilters) {
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
        return candidateComponents;
    }
    protected void registerCandidateComponents(Set<ScannedGenericBeanDefinition> candidateComponents) {
        for (ScannedGenericBeanDefinition candidate : candidateComponents) {
            Map<String, Object> attributes = ((ScannedGenericBeanDefinition)candidate).getMetadata()
                    .getAnnotationAttributes(annotationType.getName());
            if (attributes != null) {
                doRegister(attributes,(ScannedGenericBeanDefinition)candidate);
            }
        }
    }
    
    protected void doRegister(Map<String, Object> attributes, ScannedGenericBeanDefinition beanDefinition) {
    }
}
