package com.polaris.core.connect;

public interface ServerDiscoveryHandler {

	String getUrl(String key);
	
	String[] getAllUrls(String key);

	void connectionFail(String key, String url);
}
