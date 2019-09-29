package com.polaris.core.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.stereotype.Component;

import com.polaris.core.config.DefaultRootConfig;

@Component
public class SpringUtil implements ApplicationContextAware {
	private static ApplicationContext context = null;
	
	/*

     * 实现了ApplicationContextAware 接口，必须实现该方法；

     *通过传递applicationContext参数初始化成员变量applicationContext

     */
    @SuppressWarnings("static-access")
    @Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    	this.context = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
           return context;
    }
    
    public static Object getBean(String serviceName){
    	if (context == null) {
    		return null;
    	}
    	try {
            return context.getBean(serviceName);
    	} catch (Exception ex) {
    		return null;
    	}
    }

    public static <T> T getBean(Class<T> requiredType){
    	if (context == null) {
    		return null;
    	}
    	try {
        	return context.getBean(requiredType);
    	} catch (Exception ex) {
    		return null;
    	}
    }

    public static void refresh() {
    	if (context == null) {
    		return;
    	}
    	synchronized(SpringUtil.class) {
    		((AbstractApplicationContext)context).refresh();    	
    	}
    }
    
    public static void start(Class<?>... clazzs) {
    	@SuppressWarnings("resource")
		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
    	applicationContext.register(DefaultRootConfig.class);
    	if (clazzs != null && clazzs.length > 0) {
    		applicationContext.register(clazzs);
    	}
		applicationContext.refresh();
		applicationContext.registerShutdownHook();
    }


}
