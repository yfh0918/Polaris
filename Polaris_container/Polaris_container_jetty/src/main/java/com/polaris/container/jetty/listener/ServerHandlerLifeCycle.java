package com.polaris.container.jetty.listener;

import java.util.ServiceLoader;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;

import org.eclipse.jetty.util.component.AbstractLifeCycle;

public class ServerHandlerLifeCycle extends AbstractLifeCycle {
	ServletContext sc;
	private final ServiceLoader<ServletContainerInitializer> serviceLoader = ServiceLoader.load(ServletContainerInitializer.class);
	
	public ServerHandlerLifeCycle(ServletContext sc) {
		this.sc = sc;
	}
	protected void doStart() throws Exception
    {
		for (ServletContainerInitializer servletContainerInitializer : serviceLoader) {
			try {
				servletContainerInitializer.onStartup(null, sc);
			} catch (Exception e) {
				//nothing
			}
		}
    }
}
