package com.polaris.server.factory;

import com.polaris.server.listener.ServerListener;

public interface ContainerDiscoveryHandler {
	void start(ServerListener listener);
	default void stop() {	
	}
}
