package com.polaris.container.servlet.initializer;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import com.polaris.core.OrderWrapper;

@SuppressWarnings("rawtypes")
public class ServletInitializerImpl implements  ServletContainerInitializer { 
	
	private static final ServiceLoader<ExtensionInitializer> servlets = ServiceLoader.load(ExtensionInitializer.class);
	private static volatile AtomicBoolean initialized = new AtomicBoolean(false);
	private static List<OrderWrapper> servletList = new ArrayList<OrderWrapper>();
    private static volatile ExtensionInitializer extensionInitializer;

    public void onStartup(Set<Class<?>> requestFirstFilters, ServletContext servletContext)  throws ServletException {
		
    	//初始化
		if (initialized.compareAndSet(false, true)) {
			for (ExtensionInitializer extensionInitializer : servlets) {
	    		OrderWrapper.insertSorted(servletList, extensionInitializer);
	        }
	    	if (servletList.size() > 0) {
	    		extensionInitializer = (ExtensionInitializer)servletList.get(0).getHandler();
	    	}
		}
		
		//contex为空直接返回
		if (servletContext == null || extensionInitializer == null) {
    		throw new RuntimeException("Polaris_container_servlet_xxx is not found, please check the pom.xml");
		}
		
		//加载外部扩展的filter,Listener,servlet
		extensionInitializer.onStartup(servletContext);
	} 
	

}
