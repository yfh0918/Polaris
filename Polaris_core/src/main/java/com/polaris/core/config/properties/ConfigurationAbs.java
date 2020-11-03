package com.polaris.core.config.properties;

import java.lang.annotation.Annotation;

import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.annotation.AnnotatedElementUtils;

public abstract class ConfigurationAbs implements BeanPostProcessor, PriorityOrdered{
    protected <A extends Annotation> A getAnnotation(Object bean, String beanName, Class<A> type) {
        return AnnotatedElementUtils.findMergedAnnotation(bean.getClass(), type);
    }
}
