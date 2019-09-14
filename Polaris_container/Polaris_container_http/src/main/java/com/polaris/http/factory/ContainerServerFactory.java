package com.polaris.http.factory;

import java.util.ServiceLoader;

import com.polaris.http.listener.ServerListener;

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
    public static void stopServer() {
    	for (ContainerDiscoveryHandler container : containers) {
    		container.stop();
    		break;
		}
    }
}
