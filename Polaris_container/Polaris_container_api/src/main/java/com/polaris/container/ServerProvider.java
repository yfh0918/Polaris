package com.polaris.container;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.atomic.AtomicBoolean;

import com.polaris.core.OrderWrapper;

@SuppressWarnings("rawtypes")
public class ServerProvider {
	private static final ServiceLoader<Server> servers = ServiceLoader.load(Server.class);
	private static volatile AtomicBoolean initialized = new AtomicBoolean(false);
	private static List<OrderWrapper> serverList = new ArrayList<OrderWrapper>();
    private static volatile Server server;
	private ServerProvider() {}
	
    public static Server getServer() {
    	if (initialized.compareAndSet(false, true)) {
    		for (Server server : servers) {
        		OrderWrapper.insertSorted(serverList, server);
            }
        	if (serverList.size() > 0) {
        		server = (Server)serverList.get(0).getHandler();
        	}
        }
    	if (server == null) {
    		throw new RuntimeException("Polaris_container_xxx is not found, please check the pom.xml");
    	}
    	return server;
    }
}
