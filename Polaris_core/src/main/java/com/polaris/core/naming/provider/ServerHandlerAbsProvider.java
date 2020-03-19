package com.polaris.core.naming.provider;

import java.util.ArrayList;
import java.util.List;

public abstract class ServerHandlerAbsProvider {
	
	public static final String HTTP_PREFIX = "http://";
	public static final String HTTPS_PREFIX = "https://";

	protected List<String> parseServer(String serverInfo) {
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
	
	
    protected abstract boolean register(String ip, int port);
    
    protected abstract boolean deregister(String ip, int port);

	protected abstract String getUrl(String key);
	
	protected abstract boolean connectionFail(String key, String url);
    
	protected abstract void reset();
	
	protected abstract List<String> getAllUrl(String key);
	
	protected abstract List<String> getAllUrl(String key, boolean subscribe);

}
