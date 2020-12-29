package com.polaris.container.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.core.component.LifeCycle;
import com.polaris.core.component.LifeCycleListener;
import com.polaris.core.component.ManagedComponent;
import com.polaris.core.thread.ThreadPoolBuilder;

public class ServerListenerProxy implements LifeCycleListener{
	private static final Logger logger = LoggerFactory.getLogger(ServerListenerProxy.class);
	private List<ServerListener> serverListenerList = new ArrayList<>();
	
	public static ServerListenerProxy INSTANCE = new ServerListenerProxy();
    private ServerListenerProxy() {}
    
	public void init(String[] arg, ServerListener... serverListeners) {
		addServerListener(serverListeners);
		addServerListenerExtension();
		addServerListener(new ManagedComponentListener());
		addServerListener(new ThreadPoolListerner());
	}
	
	private void addServerListener(ServerListener... serverListeners) {
		if (serverListeners != null && serverListeners.length > 0) {
    		for (ServerListener serverListener : serverListeners) {
    			serverListenerList.add(serverListener);
    		}
    	}
	}
	private void addServerListenerExtension() {
		ServiceLoader<ServerListenerExtension> serverListenerExtensions = ServiceLoader.load(ServerListenerExtension.class);
		for (ServerListenerExtension serverListenerExtension : serverListenerExtensions) {
			ServerListener[] serverListeners = serverListenerExtension.getServerListeners();
			if (serverListeners != null && serverListeners.length > 0) {
				for (ServerListener serverListener : serverListeners) {
					serverListenerList.add(serverListener);
				}
			}
        }
	}
	
	@Override
	public void starting(LifeCycle event) {
		for (ServerListener serverListener : serverListenerList) {
			logger.debug("serverListener:{} starting",serverListener.getClass().getName());
			serverListener.starting(event);
		}
	}
	
	@Override
	public void started(LifeCycle event) {
		for (ServerListener serverListener : serverListenerList) {
			logger.debug("serverListener:{} started",serverListener.getClass().getName());
			serverListener.started(event);
		}
	}
	
	@Override
	public void failure(LifeCycle event, Throwable cause) {
		for (ServerListener serverListener : serverListenerList) {
			logger.debug("serverListener:{} failure",serverListener.getClass().getName());
			serverListener.failure(event,cause);
		}
	}
	
	@Override
	public void stopping(LifeCycle event) {
		for (ServerListener serverListener : serverListenerList) {
			logger.debug("serverListener:{} stopping",serverListener.getClass().getName());
			serverListener.stopping(event);
		}
	}
	
	@Override
	public void stopped(LifeCycle event) {
		for (ServerListener serverListener : serverListenerList) {
			logger.debug("serverListener:{} stopped",serverListener.getClass().getName());
			serverListener.stopped(event);
		}
	}
	
	protected static class ManagedComponentListener implements ServerListener {
		@Override
		public void started(LifeCycle event) {
			ManagedComponent.init();
			
		}
		@Override
		public void stopped(LifeCycle event) {
			ManagedComponent.destroy();;
		}
	}
	
	protected static class ThreadPoolListerner implements ServerListener {
	    @Override
        public void started(LifeCycle event) {
	        ThreadPoolBuilder.init();
            
        }
	    
		@Override
		public void stopped(LifeCycle event) {
			ThreadPoolBuilder.destroy();
		}

	}
}
