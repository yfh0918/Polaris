package com.polaris.core.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.polaris.core.config.DefaultRootConfig;

public class SpringUtil {
	private static ApplicationContext context = null;
	
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
    
    public static void setApplicationContext(ApplicationContext inputContext){
    	context = inputContext;
    }
    

    
    public synchronized static void refresh(Class<?>... clazzs) {
    	if (context == null) {
    		AnnotationConfigApplicationContext annotationContext = new AnnotationConfigApplicationContext();
	    	annotationContext.register(DefaultRootConfig.class);
	    	if (clazzs != null && clazzs.length > 0) {
				annotationContext.register(clazzs);
	    	}
	    	annotationContext.refresh();
	    	annotationContext.registerShutdownHook();
	    	context = annotationContext;
    	}
    }


}
