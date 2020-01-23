package com.polaris.container;

import java.util.ServiceLoader;

public class ServerFactory {
	private static final ServiceLoader<Server> servers = ServiceLoader.load(Server.class);
	private ServerFactory() {}
    private static volatile Server server;
	
    public static Server getServer() {
    	if (server == null) {
    		synchronized (Server.class) {
    			if (server == null) {
    				int count  = 0;
    		    	for (Server tempServer : servers) {
    		    		server = tempServer;
    		    		count++;
    				}
    		    	if (count != 1) {
    		    		throw new RuntimeException("Container's number is bigger than 1");
    		    	}
    			}
    		}
    	}
    	return server;
    }
}
