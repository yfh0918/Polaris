package com.polaris.container.listener;

import java.util.ArrayList;
import java.util.List;

public abstract class ServerListenerSupport {
	
	private static List<ServerListener> serverListenerList = new ArrayList<>();
	
	public static void add(ServerListener... serverListeners) {
		if (serverListeners != null && serverListeners.length > 0) {
    		for (ServerListener serverListener : serverListeners) {
    			serverListenerList.add(serverListener);
    		}
    	}
	}
	public static void add(ServerListener[] serverListeners, ServerListener serverListener) {
		serverListenerList.add(serverListener);
		ServerListenerSupport.add(serverListeners);
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
}
