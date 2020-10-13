package com.polaris.core.config.properties;

import java.lang.annotation.Annotation;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.annotation.AnnotatedElementUtils;

public abstract class ConfigurationAbs implements BeanPostProcessor, PriorityOrdered, ApplicationContextAware, InitializingBean{
    protected <A extends Annotation> A getAnnotation(Object bean, String beanName, Class<A> type) {
        return AnnotatedElementUtils.findMergedAnnotation(bean.getClass(), type);
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    }
}
