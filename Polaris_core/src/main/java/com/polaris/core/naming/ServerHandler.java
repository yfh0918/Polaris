package com.polaris.core.naming;

import java.util.List;

public interface ServerHandler {
	boolean register(String ip, int port);
	boolean deregister(String ip, int port);
	String getUrl(String key);
	List<String> getAllUrls(String key);
	List<String> getAllUrls(String key, boolean subscribe);
	boolean connectionFail(String key, String url);
}
