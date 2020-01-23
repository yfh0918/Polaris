package com.polaris.container;

import com.polaris.container.servlet.listener.ServerListener;

public interface Server {
	public void start(ServerListener listener);
	public default void stop() {}
	public default Object getContext() {return null;}
}
