package com.polaris.core.naming.provider;

import java.util.List;

public class ServerStrategyProvider extends ServerHandlerAbsProvider{
    public static final ServerStrategyProvider INSTANCE = new ServerStrategyProvider();
    private static final ServerHandlerRemoteProvider INSTANCE_REMOTE = ServerHandlerRemoteProvider.INSTANCE;
    private static final ServerHandlerLocalProvider INSTANCE_LOCAL = ServerHandlerLocalProvider.INSTANCE;
    private ServerStrategyProvider() {}
    
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
			url = INSTANCE_REMOTE.getUrl(key);
		}
		return url == null ? INSTANCE_LOCAL.getUrl(key) : url;
	}

	@Override
	public List<String> getAllUrl(String key) {
		List<String> urls = null;
		List<String> serverInfoList = parseServer(key);
		if (isRemote(serverInfoList.get(1))) {
			urls = INSTANCE_REMOTE.getAllUrl(key);
		}
		return urls == null ? INSTANCE_LOCAL.getAllUrl(key) : urls;
	}
	
	@Override
	public List<String> getAllUrl(String key, boolean subscribe) {
		List<String> urls = null;
		List<String> serverInfoList = parseServer(key);
		if (isRemote(serverInfoList.get(1))) {
			urls = INSTANCE_REMOTE.getAllUrl(key,subscribe);
		}
		return urls == null ? INSTANCE_LOCAL.getAllUrl(key,subscribe) : urls;
	}

	@Override
	public boolean connectionFail(String key, String url) {
		List<String> serverInfoList = parseServer(key);
		boolean result = false;
		if (isRemote(serverInfoList.get(1))) {
			result = INSTANCE_REMOTE.connectionFail(key,url);
		}
		if (!result) {
			return INSTANCE_LOCAL.connectionFail(key, url);
		}
		return true;
	}
	
	@Override
	public void reset() {
		INSTANCE_REMOTE.reset();
		INSTANCE_LOCAL.reset();
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

}
