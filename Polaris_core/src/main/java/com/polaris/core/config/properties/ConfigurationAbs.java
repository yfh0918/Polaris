package com.polaris.core.config.properties;

import java.lang.annotation.Annotation;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.annotation.AnnotatedElementUtils;

import com.polaris.core.config.ConfigFactory;
import com.polaris.core.config.Config.Type;
import com.polaris.core.config.provider.ConfHandlerProviderFactory;
import com.polaris.core.exception.ConfigException;
import com.polaris.core.util.StringUtil;

public abstract class ConfigurationAbs implements BeanPostProcessor, PriorityOrdered, ApplicationContextAware, InitializingBean{
    protected <A extends Annotation> A getAnnotation(Object bean, String beanName, Class<A> type) {
        return AnnotatedElementUtils.findMergedAnnotation(bean.getClass(), type);
    }
    
    protected void init(Object bean, String[] files, Type type) {
        for (String file : files) {
            if (StringUtil.isNotEmpty(file)) {
                if (ConfigFactory.get(type).getProperties(file) == null) {
                    if (!ConfHandlerProviderFactory.get(type).getAndListen(file)) {
                        throw new ConfigException("type:"+type.toString()+" file:" + file + " is not exsit");
                    }
                } 
            } 
        }
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    }
}
