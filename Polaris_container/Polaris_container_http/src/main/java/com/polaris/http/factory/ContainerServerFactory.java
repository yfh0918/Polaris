package com.polaris.http.factory;

import java.util.ServiceLoader;

import com.polaris.http.supports.ServerListener;

public class ContainerServerFactory {
	private static final ServiceLoader<ContainerDiscoveryHandler> containers = ServiceLoader.load(ContainerDiscoveryHandler.class);
    private ContainerServerFactory() {
    }

    public static void startServer(ServerListener listener) {
    	for (ContainerDiscoveryHandler container : containers) {
    		container.start(listener);
    		break;
		}
    }
}
