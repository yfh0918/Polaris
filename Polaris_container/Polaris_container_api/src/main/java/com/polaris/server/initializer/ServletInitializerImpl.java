package com.polaris.server.initializer;

import java.util.ServiceLoader;
import java.util.Set;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

public class ServletInitializerImpl implements  ServletContainerInitializer { 
	private final ServiceLoader<ExtensionInitializer> serviceLoader = ServiceLoader.load(ExtensionInitializer.class);
	public void onStartup(Set<Class<?>> requestFirstFilters, ServletContext servletContext)  throws ServletException {
		
		//contex为空直接返回
		if (servletContext == null) {
			return;
		}
		
		//加载外部扩展的filter,Listener,servlet
		for (ExtensionInitializer httpInitializer : serviceLoader) {
			httpInitializer.onStartup(servletContext);
			break;
		}
	} 
	

}
