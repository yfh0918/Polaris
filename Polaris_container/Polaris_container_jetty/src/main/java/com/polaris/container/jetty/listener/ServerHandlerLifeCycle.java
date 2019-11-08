package com.polaris.container.jetty.listener;

import java.util.HashSet;
import java.util.ServiceLoader;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;

import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.servlet.ServletContextHandler.ServletContainerInitializerCaller;

public class ServerHandlerLifeCycle implements ServletContainerInitializerCaller {
	ServletContext sc;
	private final ServiceLoader<ServletContainerInitializer> serviceLoader = ServiceLoader.load(ServletContainerInitializer.class);
	
	public ServerHandlerLifeCycle(ServletContext sc) {
		this.sc = sc;
	}
	protected void doStart() throws Exception
    {
		for (ServletContainerInitializer servletContainerInitializer : serviceLoader) {
			try {
				ContextHandler.getCurrentContext().setExtendedListenerTypes(true);
				servletContainerInitializer.onStartup(new HashSet<>(), sc);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				ContextHandler.getCurrentContext().setExtendedListenerTypes(false);
			}
		}
    }
	@Override
	public void start() throws Exception {
		doStart();
	}
	@Override
	public void stop() throws Exception {
		// TODO Auto-generated method stub
		
	}
	@Override
	public boolean isRunning() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean isStarted() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean isStarting() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean isStopping() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean isStopped() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean isFailed() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public void addLifeCycleListener(Listener listener) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void removeLifeCycleListener(Listener listener) {
		// TODO Auto-generated method stub
		
	}
}
