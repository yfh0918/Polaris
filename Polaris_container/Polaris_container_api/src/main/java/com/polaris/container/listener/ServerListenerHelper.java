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
		addServerListener(new LifeCycleRegisterServerListener());
		addServerListener(new ServerRegisterServerListener());
		starting();
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
			logger.info("serverListener:{} starting",serverListener.getClass().getSimpleName());
			serverListener.starting();
		}
	}
	public static void started() {
		for (ServerListener serverListener : serverListenerList) {
			logger.info("serverListener:{} started",serverListener.getClass().getSimpleName());
			serverListener.started();
		}
	}
	public static void failure() {
		for (ServerListener serverListener : serverListenerList) {
			logger.info("serverListener:{} failure",serverListener.getClass().getSimpleName());
			serverListener.failure();
		}
	}
	public static void stopping() {
		for (ServerListener serverListener : serverListenerList) {
			logger.info("serverListener:{} stopping",serverListener.getClass().getSimpleName());
			serverListener.stopping();
		}
	}
	public static void stopped() {
		for (ServerListener serverListener : serverListenerList) {
			logger.info("serverListener:{} stopped",serverListener.getClass().getSimpleName());
			serverListener.stopped();
		}
	}
	
	protected static class ServerRegisterServerListener implements ServerListener {
		@Override
		public void started() {
			ServerClient.register();
			
		}
		@Override
		public void stopped() {
	    	ServerClient.unRegister();
		}
	}
	
	protected static class LifeCycleRegisterServerListener implements ServerListener {
		@Override
		public void stopped() {
	    	LifeCycleManager.close();
		}
	}

}
