package com.polaris.container.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.core.component.LifeCycle;
import com.polaris.core.component.ManagedComponent;
import com.polaris.core.naming.ServerClient;

public abstract class ServerListenerHelper {
	private static final Logger logger = LoggerFactory.getLogger(ServerListenerHelper.class);
	private static List<ServerListener> serverListenerList = new ArrayList<>();
	
	public static void init(String[] arg, ServerListener... serverListeners) {
		addServerListener(serverListeners);
		addServerListenerExtension();
		addServerListener(new ManagedComponentListener());
	}
	
	public static void addServerListener(ServerListener... serverListeners) {
		if (serverListeners != null && serverListeners.length > 0) {
    		for (ServerListener serverListener : serverListeners) {
    			serverListenerList.add(serverListener);
    		}
    	}
	}
	public static void addServerListenerExtension() {
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

	public static void starting(LifeCycle event) {
		for (ServerListener serverListener : serverListenerList) {
			logger.debug("serverListener:{} starting",serverListener.getClass().getName());
			serverListener.starting(event);
		}
	}
	public static void started(LifeCycle event) {
		for (ServerListener serverListener : serverListenerList) {
			logger.debug("serverListener:{} started",serverListener.getClass().getName());
			serverListener.started(event);
		}
		// the last one is  register
		ServerClient.register();
	}
	public static void failure(LifeCycle event, Throwable cause) {
		for (ServerListener serverListener : serverListenerList) {
			logger.debug("serverListener:{} failure",serverListener.getClass().getName());
			serverListener.failure(event,cause);
		}
	}
	public static void stopping(LifeCycle event) {
		// The first one to unRegister 
		ServerClient.unRegister();
		for (ServerListener serverListener : serverListenerList) {
			logger.debug("serverListener:{} stopping",serverListener.getClass().getName());
			serverListener.stopping(event);
		}
	}
	public static void stopped(LifeCycle event) {
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

}
