package com.polaris.container.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.core.component.LifeCycleManager;
import com.polaris.core.naming.ServerClient;

public abstract class ServerListenerHelper {
	private static final Logger logger = LoggerFactory.getLogger(ServerListenerHelper.class);
	private static List<ServerListener> serverListenerList = new ArrayList<>();
	
	public static void init(String[] arg, ServerListener... serverListeners) {
		addServerListener(serverListeners);
		addServerListenerExtension();
		addServerListener(new LifeCycleListener());
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

	public static void starting() {
		for (ServerListener serverListener : serverListenerList) {
			logger.debug("serverListener:{} starting",serverListener.getClass().getName());
			serverListener.starting();
		}
	}
	public static void started() {
		for (ServerListener serverListener : serverListenerList) {
			logger.debug("serverListener:{} started",serverListener.getClass().getName());
			serverListener.started();
		}
		
		// The last started to register 
		ServerClient.register();
	}
	public static void failure() {
		for (ServerListener serverListener : serverListenerList) {
			logger.debug("serverListener:{} failure",serverListener.getClass().getName());
			serverListener.failure();
		}
	}
	public static void stopping() {
		// The first stopping to unRegister 
		ServerClient.unRegister();
		
		for (ServerListener serverListener : serverListenerList) {
			logger.debug("serverListener:{} stopping",serverListener.getClass().getName());
			serverListener.stopping();
		}
	}
	public static void stopped() {
		for (ServerListener serverListener : serverListenerList) {
			logger.debug("serverListener:{} stopped",serverListener.getClass().getName());
			serverListener.stopped();
		}
	}
	
	protected static class LifeCycleListener implements ServerListener {
		@Override
		public void started() {
			LifeCycleManager.start();
			
		}
		@Override
		public void stopped() {
	    	LifeCycleManager.stop();
		}
	}

}
