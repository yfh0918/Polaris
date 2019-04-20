package com.polaris.http.factory;

import java.util.ServiceLoader;

public class ContainerServerFactory {
	private static final ServiceLoader<ContainerDiscoveryHandler> containers = ServiceLoader.load(ContainerDiscoveryHandler.class);
    private ContainerServerFactory() {
    }

    public static void startServer() {
    	for (ContainerDiscoveryHandler container : containers) {
    		container.start();
    		break;
		}
    }
}
