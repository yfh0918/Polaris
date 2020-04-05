package com.polaris.core.naming.provider;

import java.util.ArrayList;
import java.util.List;

public class ServerStrategyProviderDefault implements ServerStrategyProvider{
	private static final String HTTP_PREFIX = "http://";
	private static final String HTTPS_PREFIX = "https://";
    public static final ServerStrategyProviderDefault INSTANCE = new ServerStrategyProviderDefault();
    private static final ServerHandlerRemoteProvider INSTANCE_REMOTE = ServerHandlerRemoteProvider.INSTANCE;
    private static final ServerHandlerLocalProvider INSTANCE_LOCAL = ServerHandlerLocalProvider.INSTANCE;
    private ServerStrategyProviderDefault() {}
    
    @Override
    public boolean register(String ip, int port) {
    	return INSTANCE_REMOTE.register(ip, port);
    }
    
    @Override
    public boolean deregister(String ip, int port) {
    	return INSTANCE_REMOTE.deregister(ip, port);
    }
    
    @Override
	public String getUrl(String key) {
    	String url = null;
    	List<String> serverInfoList = parseServer(key);
		if (isRemote(serverInfoList.get(1))) {
			url = INSTANCE_REMOTE.getUrl(key,serverInfoList);
		}
		return url == null ? INSTANCE_LOCAL.getUrl(key,serverInfoList) : url;
	}

    @Override
	public List<String> getAllUrls(String key) {
		List<String> urls = null;
		List<String> serverInfoList = parseServer(key);
		if (isRemote(serverInfoList.get(1))) {
			urls = INSTANCE_REMOTE.getAllUrl(key,serverInfoList);
		}
		return urls == null ? INSTANCE_LOCAL.getAllUrl(key,serverInfoList) : urls;
	}
	
    @Override
	public List<String> getAllUrls(String key, boolean subscribe) {
		List<String> urls = null;
		List<String> serverInfoList = parseServer(key);
		if (isRemote(serverInfoList.get(1))) {
			urls = INSTANCE_REMOTE.getAllUrl(key,serverInfoList,subscribe);
		}
		return urls == null ? INSTANCE_LOCAL.getAllUrl(key,serverInfoList,subscribe) : urls;
	}

    @Override
	public boolean connectionFail(String key, String url) {
		
		List<String> serverInfoList = parseServer(key);
		List<String> serverInfoList2 = parseServer(url);
		boolean result = false;
		if (isRemote(serverInfoList.get(1))) {
			result = INSTANCE_REMOTE.connectionFail(serverInfoList.get(1), serverInfoList2.get(1));
		}
		if (!result) {
			return INSTANCE_LOCAL.connectionFail(serverInfoList.get(1), serverInfoList2.get(1));
		}
		return true;
	}
	
    @Override
	public void init() {
		INSTANCE_LOCAL.init();
	}
	
	private boolean isRemote(String key) {
		if (key.toLowerCase().startsWith("www.")) {
			return false;
		}
		if (key.toLowerCase().endsWith(".com") || key.toLowerCase().endsWith(".cn")) {
			return false;
		}
		if (key.contains(",") || key.contains(":")) {
			return false;
		}
		return true;
	}
	
	private List<String> parseServer(String serverInfo) {
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


}
