package com.polaris.server.factory;

import com.polaris.server.listener.ServerListener;

public interface Container {
	public void start(ServerListener listener);
	public default void stop() {}
	public default Object getContext() {return null;}
}
