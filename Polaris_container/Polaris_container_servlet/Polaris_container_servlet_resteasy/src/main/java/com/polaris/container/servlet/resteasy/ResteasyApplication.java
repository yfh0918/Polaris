package com.polaris.container.servlet.resteasy;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Path;
import javax.ws.rs.core.Application;

import com.polaris.core.util.SpringUtil;

public class ResteasyApplication extends Application {
	private static Set<Object> singletons = new LinkedHashSet<Object>();
    private static Set<Class<?>> classes = new LinkedHashSet<Class<?>>();
 
    public ResteasyApplication() {
    	
		Map<String,Object> paths = SpringUtil.getApplicationContext().getBeansWithAnnotation(Path.class);
		if (paths != null && paths.size() > 0) {
	        for (Object path : paths.values()) {
	        	singletons.add(path);
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
    public static void registerEndPoint(Object obj) {
    	//singleton
    	Path path = obj.getClass().getAnnotation(Path.class);
    	if (path != null) {
        	singletons.add(obj);
    	}
    }
    public static void registerEndPoint(Class<?> clazz) {
    	//prototype(per request new instance)
    	Path path = clazz.getAnnotation(Path.class);
    	if (path != null) {
        	classes.add(clazz);
    	}
    }
}
