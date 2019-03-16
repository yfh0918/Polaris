package com.polaris.core.connect;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import com.polaris.comm.util.StringUtil;

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
		List<String> temp = getRemoteAddress(key);
		// 单个IP或者多IP不走注册中心
		if (!isSkip(temp.get(1))) {
			
			//走注册中心
			for (ServerDiscoveryHandler handler : serviceLoader) {
				
				
				// 走注册中心
				String url = handler.getUrl(temp.get(1));
				if (StringUtil.isNotEmpty(url)) {
					return temp.get(0) + url + temp.get(2);
				}
				
				//只走第一个注册中心
				break;
			}
		}
		return temp.get(0) + ServerDiscovery.getUrl(temp.get(1)) + temp.get(2);
	}


	
	public List<String> getAllUrl(String key) {
		
		List<String> temp = getRemoteAddress(key);
		// 单个IP或者多IP不走注册中心
		if (isSkip(temp.get(1))) {
			List<String> urlList = new ArrayList<>();
			String[] ips = temp.get(1).split(",");
			for (int i0 = 0; i0 < ips.length; i0++) {
				urlList.add(temp.get(0) + ips[i0] + temp.get(2));
			}
			return urlList;
		}

		// 走注册中心
		for (ServerDiscoveryHandler handler : serviceLoader) {
			List<String> urls = handler.getAllUrls(temp.get(1));
			for (int i0 = 0; i0 < urls.size(); i0++) {
				String value = temp.get(0) + urls.get(i0) + temp.get(2);
				urls.set(i0, value);
			}
			return urls;			
		}
		return null;
	}

	public void connectionFail(String key, String url) {
		List<String> temp = getRemoteAddress(key);
		List<String> temp2 = getRemoteAddress(url);
		// 单个IP或者多IP不走注册中心
		if (!isSkip(temp.get(1))) {
			for (ServerDiscoveryHandler handler : serviceLoader) {
				handler.connectionFail(temp.get(1), temp2.get(1));
				return;
			}
		}
		ServerDiscovery.connectionFail(temp.get(1), temp2.get(1));
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
	private boolean isSkip(String key) {
		if (key.toLowerCase().startsWith("www.")) {
			return true;
		}
		if (key.toLowerCase().endsWith(".com") || key.toLowerCase().endsWith(".cn")) {
			return true;
		}
		if (key.contains(",") || key.contains(":")) {
			return true;
		}
		return false;
	}
	//用于重新reset
	public void reset() {
		//注册中心的reset由注册中心自己完成
		ServerDiscovery.reset();
	}
	
	public static void main(String[] args) {
		String key = "http://localhost:8080,localhost:8081/test/tesaa/afad";
		for (int i0 = 0; i0 < 10; i0++) {
			System.out.println(ServerDiscoveryHandlerProvider.INSTANCE.getUrl(key));
		}
		ServerDiscoveryHandlerProvider.INSTANCE.connectionFail(key, "http://localhost:8080/test/tesaa/afad");
		for (int i0 = 0; i0 < 10; i0++) {
			System.out.println(ServerDiscoveryHandlerProvider.INSTANCE.getUrl(key));
		}
    }
}
