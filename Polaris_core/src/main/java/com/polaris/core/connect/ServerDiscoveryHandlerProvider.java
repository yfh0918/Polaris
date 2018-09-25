package com.polaris.core.connect;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

public class ServerDiscoveryHandlerProvider {
private final ServiceLoader<ServerDiscoveryHandler> serviceLoader = ServiceLoader.load(ServerDiscoveryHandler.class);
	public static final String HTTP_PREFIX = "http://";
	public static final String HTTPS_PREFIX = "https://";

    private static final ServerDiscoveryHandlerProvider INSTANCE = new ServerDiscoveryHandlerProvider();

    public static ServerDiscoveryHandlerProvider getInstance() {
        return INSTANCE;
    }
    
    public void register(String ip, int port) {
    	for (ServerDiscoveryHandler handler : serviceLoader) {
			handler.register(ip, port);
		}
    }
    
    public void deregister(String ip, int port) {
    	for (ServerDiscoveryHandler handler : serviceLoader) {
			handler.deregister(ip, port);
		}
    }

	public String getUrl(String key) {
		for (ServerDiscoveryHandler handler : serviceLoader) {
			List<String> temp = getRemoteAddress(key);
			return temp.get(0) + handler.getUrl(temp.get(1)) + temp.get(2);
		}
		return ServerDiscovery.getUrl(key);
	}

	public String[] getAllUrl(String key) {
		for (ServerDiscoveryHandler handler : serviceLoader) {
			List<String> temp = getRemoteAddress(key);
			String[] urls = handler.getAllUrls(temp.get(1));
			for (int i0 = 0; i0 < urls.length; i0++) {
				urls[i0] = temp.get(0) + urls[i0] + temp.get(2);
			}
			return urls;
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
	
	private List<String> getRemoteAddress(String serverInfo) {
		List<String> serverList = new ArrayList<>(3);
		if (serverInfo.toLowerCase().startsWith(HTTP_PREFIX)) {
			serverList.add(HTTP_PREFIX);
			serverInfo = serverInfo.substring(HTTP_PREFIX.length());
		} else if (serverInfo.toLowerCase().startsWith(HTTPS_PREFIX)) {
			serverList.add(HTTPS_PREFIX);
			serverInfo = serverInfo.substring(HTTPS_PREFIX.length());
		} else {
			serverList.add("");
		}
		int suffixIndex = serverInfo.indexOf("/");
		if (suffixIndex > 0) {
			serverList.add(serverInfo.substring(0, suffixIndex));
			serverList.add(serverInfo.substring(suffixIndex));
		} else {
			serverList.add(serverInfo);
			serverList.add("");
		}
        return serverList;
    }
	
	public static void main(String[] args) {
		List<String> url = ServerDiscoveryHandlerProvider.getInstance().getRemoteAddress("http://localhost:8080/test/tesaa/afad");
		url = ServerDiscoveryHandlerProvider.getInstance().getRemoteAddress("http://localhost:8080");
		url = ServerDiscoveryHandlerProvider.getInstance().getRemoteAddress("localhost:8080/test/tesaa/afad");
		url = ServerDiscoveryHandlerProvider.getInstance().getRemoteAddress("localhost:8080");
		System.out.println(url);
    }
}
