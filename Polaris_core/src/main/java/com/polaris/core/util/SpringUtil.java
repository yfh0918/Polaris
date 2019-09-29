package com.polaris.core.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;

import com.polaris.core.config.DefaultRootConfig;

public class SpringUtil {
	private static AnnotationConfigApplicationContext context = null;
	
    public static ApplicationContext getApplicationContext() {
           return context;
    }
    
    public static Object getBean(String serviceName){
    	try {
            return context.getBean(serviceName);
    	} catch (Exception ex) {
    		return null;
    	}
    }

    public static <T> T getBean(Class<T> requiredType){
    	try {
        	return context.getBean(requiredType);
    	} catch (Exception ex) {
    		return null;
    	}
    }

    
    public synchronized static void refresh(Class<?>... clazzs) {
    	if (context == null) {
        	context = new AnnotationConfigApplicationContext();
    	}
    	context.register(DefaultRootConfig.class);
    	if (clazzs != null && clazzs.length > 0) {
    		for (Class<?> clazz : clazzs) {
    			if (clazz.getAnnotation(Configuration.class) != null) {
    				context.register(clazzs);
    			}
    		}
    		
    	}
    	context.refresh();
		context.registerShutdownHook();
    }


}
