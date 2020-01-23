package com.polaris.server.factory;

import java.util.ServiceLoader;

import javax.servlet.ServletContext;

import com.polaris.server.listener.ServerListener;

public class ContainerServerFactory {
	private static final ServiceLoader<ContainerDiscoveryHandler> containers = ServiceLoader.load(ContainerDiscoveryHandler.class);
	private ContainerServerFactory() {}
    

    public static void startServer(ServerListener listener) {
    	int count  = 0;
    	ContainerDiscoveryHandler container = null;
    	for (ContainerDiscoveryHandler tempContainer : containers) {
    		container = tempContainer;
    		count++;
		}
    	if (count != 1) {
    		throw new RuntimeException("serverContainer's number is bigger than 1");
    	}
		container.start(listener);
    	
    }
    public static void stopServer() {
    	for (ContainerDiscoveryHandler container : containers) {
    		container.stop();
    		break;
		}
    }
    public static ServletContext getServletContext() {
    	for (ContainerDiscoveryHandler container : containers) {
    		return container.getServletContex();
    	}
    	return null;
    }
    
}
