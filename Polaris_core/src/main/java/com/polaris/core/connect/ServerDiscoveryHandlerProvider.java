package com.polaris.core.connect;

import java.util.ServiceLoader;

public class ServerDiscoveryHandlerProvider {
private final ServiceLoader<ServerDiscoveryHandler> serviceLoader = ServiceLoader.load(ServerDiscoveryHandler.class);
    
    private static final ServerDiscoveryHandlerProvider INSTANCE = new ServerDiscoveryHandlerProvider();

    public static ServerDiscoveryHandlerProvider getInstance() {
        return INSTANCE;
    }

	public String getUrl(String key) {
		for (ServerDiscoveryHandler handler : serviceLoader) {
			return handler.getUrl(key);
		}
		return ServerDiscovery.getUrl(key);
	}

	public String[] getAllUrl(String key) {
		for (ServerDiscoveryHandler handler : serviceLoader) {
			return handler.getAllUrls(key);
		}
		return null;
	}

	public void connectionFail(String key, String url) {
		for (ServerDiscoveryHandler handler : serviceLoader) {
			handler.connectionFail(key, url);
			return;
		}
		ServerDiscovery.connectionFail(key, url);
	}
}
