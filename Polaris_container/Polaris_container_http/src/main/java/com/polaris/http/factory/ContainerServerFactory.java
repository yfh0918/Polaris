package com.polaris.http.factory;

import java.util.ServiceLoader;

import com.polaris.core.config.DefaultRootConfig;
import com.polaris.http.initializer.WebConfigInitializer;
import com.polaris.http.listener.ServerListener;

public class ContainerServerFactory {
	private static final ServiceLoader<ContainerDiscoveryHandler> containers = ServiceLoader.load(ContainerDiscoveryHandler.class);
    private ContainerServerFactory() {
    }

    public static void startServer(Class<?>[] rootConfig, Class<?>[] webConfig, ServerListener listener) {
    	
    	//root context
    	WebConfigInitializer.loadRootConfig(DefaultRootConfig.class);
    	if (rootConfig != null) {
    		WebConfigInitializer.loadRootConfig(rootConfig);
    	}
    	
    	//web context
    	if (webConfig != null) {
    		WebConfigInitializer.loadWebConfig(webConfig);
    	}
    	
    	//start server
    	for (ContainerDiscoveryHandler container : containers) {
    		container.start(listener);
    		break;
		}
    }
    public static void startServer(Class<?>[] rootConfig, ServerListener listener) {
    	startServer(rootConfig, null, listener);
    }
    public static void stopServer() {
    	for (ContainerDiscoveryHandler container : containers) {
    		container.stop();
    		break;
		}
    }
}
