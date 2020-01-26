package com.polaris.core.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.polaris.core.ConfigurationLoader;

public class SpringUtil {
	private static ApplicationContext context = null;
	
    public static ApplicationContext getApplicationContext() {
    	if (context == null) {
    		synchronized(SpringUtil.class) {
    			if (context == null) {
    				AnnotationConfigApplicationContext annotationContext = new AnnotationConfigApplicationContext();
    		    	annotationContext.register(ConfigurationLoader.getRootConfigClass());
    		    	context = annotationContext;
    			}
    		}
    	}
        return context;
    }
    
    public static Object getBean(String serviceName){
    	try {
            return getApplicationContext().getBean(serviceName);
    	} catch (Exception ex) {
    		return null;
    	}
    }

    public static <T> T getBean(Class<T> requiredType){
    	try {
        	return getApplicationContext().getBean(requiredType);
    	} catch (Exception ex) {
    		return null;
    	}
    }
    
    public static void setApplicationContext(ApplicationContext inputContext){
    	context = inputContext;
    }
    
    public static void refresh() {
		context = getApplicationContext();
		if (context instanceof AnnotationConfigApplicationContext) {
			((AnnotationConfigApplicationContext)context).refresh();
			((AnnotationConfigApplicationContext)context).registerShutdownHook();
		} else {
			throw new RuntimeException("context can't refresh");
		}
    }


}
