package com.polaris.core.naming;

import java.util.List;

public interface ServerDiscoveryHandler {

	void register(String ip, int port);

	void deregister(String ip, int port);

	String getUrl(String key);
	
	List<String> getAllUrls(String key);
	List<String> getAllUrls(String key, boolean subscribe);

	void connectionFail(String key, String url);
}
