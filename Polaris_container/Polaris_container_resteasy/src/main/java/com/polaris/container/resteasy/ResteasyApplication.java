package com.polaris.container.resteasy;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Path;
import javax.ws.rs.core.Application;

import com.polaris.core.util.SpringUtil;

public class ResteasyApplication extends Application {
	private static Set<Object> singletons = new HashSet<Object>();
    private static Set<Class<?>> classes = new HashSet<Class<?>>();
 
    public ResteasyApplication() {
    	
		Map<String,Object> paths = SpringUtil.getApplicationContext().getBeansWithAnnotation(Path.class);
		if (paths != null && paths.size() > 0) {
	        for (Object path : paths.values()) {
	        	singletons.add(path);
	        	classes.add(path.getClass());
	        }
		}
    }
 
    @Override
    public Set<Class<?>> getClasses() {
        return classes;
    }
 
    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }
    
    public static void registSingleton(Object obj) {
    	Path path = obj.getClass().getAnnotation(Path.class);
    	if (path != null) {
        	singletons.add(obj);
        	classes.add(obj.getClass());
    	}
    }
}
