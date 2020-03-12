package com.polaris.core.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SpringUtil {
	private static ApplicationContext context = null;
	
    public static ApplicationContext getApplicationContext() {
    	return context;
    }
    
    public static ApplicationContext createApplicationContext(Class<?>... clazz) {
    	if (context == null) {
    		synchronized(SpringUtil.class) {
    			if (context == null) {
    				AnnotationConfigApplicationContext annotationContext = new AnnotationConfigApplicationContext();
    		    	annotationContext.register(clazz);
    		    	context = annotationContext;
    			}
    		}
    	}
        return context;
    }
    
    public static Object getBean(String serviceName){
    	try {
    		if (context == null) {
    			return null;
    		}
            return getApplicationContext().getBean(serviceName);
    	} catch (Exception ex) {
    		return null;
    	}
    }

    public static <T> T getBean(Class<T> requiredType){
    	try {
    		if (context == null) {
    			return null;
    		}
        	return getApplicationContext().getBean(requiredType);
    	} catch (Exception ex) {
    		return null;
    	}
    }
    
    public static void setApplicationContext(ApplicationContext inputContext){
    	context = inputContext;
    }
    
    public static void refresh(Class<?>... clazz) {
		context = createApplicationContext(clazz);
		if (context instanceof AnnotationConfigApplicationContext) {
			((AnnotationConfigApplicationContext)context).refresh();
			((AnnotationConfigApplicationContext)context).registerShutdownHook();
		} else {
			throw new RuntimeException("context can't refresh");
		}
    }


}
