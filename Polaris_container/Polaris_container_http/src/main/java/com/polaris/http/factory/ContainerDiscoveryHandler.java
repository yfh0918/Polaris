package com.polaris.http.factory;

import com.polaris.http.listener.ServerListener;

public interface ContainerDiscoveryHandler {
	void start(ServerListener listener);
}
