package com.polaris.server.factory;

import java.util.ServiceLoader;

public class ContainerFactory {
	private static final ServiceLoader<Container> containers = ServiceLoader.load(Container.class);
	private ContainerFactory() {}
    private static volatile Container container;
	
    public static Container getContainer() {
    	if (container == null) {
    		synchronized (Container.class) {
    			if (container == null) {
    				int count  = 0;
    		    	for (Container tempContainer : containers) {
    		    		container = tempContainer;
    		    		count++;
    				}
    		    	if (count != 1) {
    		    		throw new RuntimeException("Container's number is bigger than 1");
    		    	}
    			}
    		}
    	}
    	return container;
    }
}
