package com.polaris.core.connect;

import java.util.List;

public interface ServerDiscoveryHandler {

	void register(String ip, int port);

	void deregister(String ip, int port);

	String getUrl(String key, List<String> clusters);
	
	String getUrl(String key);

	List<String> getAllUrls(String key);

	List<String> getAllUrls(String key, List<String> clusters);

	void connectionFail(String key, String url);
}
