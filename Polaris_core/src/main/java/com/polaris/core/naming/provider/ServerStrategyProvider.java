package com.polaris.core.naming.provider;

import java.util.List;

public interface ServerStrategyProvider {
    boolean register(String ip, int port);
    boolean deregister(String ip, int port);
	String getUrl(String key);
	List<String> getAllUrl(String key);
	List<String> getAllUrl(String key, boolean subscribe);
	boolean connectionFail(String key, String url);
	void reset();


}
