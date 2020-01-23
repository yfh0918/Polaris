package com.polaris.server.factory;

import javax.servlet.ServletContext;

import com.polaris.server.listener.ServerListener;

public interface ContainerDiscoveryHandler {
	void start(ServerListener listener);
	default void stop() {}
	default ServletContext getServletContex() {return null;}
}
