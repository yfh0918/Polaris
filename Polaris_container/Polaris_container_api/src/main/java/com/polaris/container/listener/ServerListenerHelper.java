package com.polaris.container.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import com.polaris.core.naming.ServerClient;

public abstract class ServerListenerHelper {
	
	private static List<ServerListener> serverListenerList = new ArrayList<>();
	
	public static void init(String[] arg, ServerListener... serverListeners) {
		addServerListener(serverListeners);
		addServerListenerExtension();
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
			serverListener.starting();
		}
	}
	public static void started() {
		for (ServerListener serverListener : serverListenerList) {
			serverListener.started();
		}
	}
	public static void failure() {
		for (ServerListener serverListener : serverListenerList) {
			serverListener.starting();
		}
	}
	public static void stopping() {
		for (ServerListener serverListener : serverListenerList) {
			serverListener.stopping();
		}
	}
	public static void stopped() {
		for (ServerListener serverListener : serverListenerList) {
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

}
