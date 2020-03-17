package com.polaris.core.naming.provider;

import java.util.List;

public class ServerCompositeProvider extends ServerHandlerAbsProvider{
    public static final ServerCompositeProvider INSTANCE = new ServerCompositeProvider();
    private static final ServerHandlerRemoteProvider INSTANCE_REMOTE = ServerHandlerRemoteProvider.INSTANCE;
    private static final ServerHandlerLocalProvider INSTANCE_LOCAL = ServerHandlerLocalProvider.INSTANCE;
    private ServerCompositeProvider() {}
    
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
		String url = INSTANCE_REMOTE.getUrl(key);
		return url == null ? INSTANCE_LOCAL.getUrl(key) : url;
	}

	@Override
	public List<String> getAllUrl(String key) {
		List<String> urls = INSTANCE_REMOTE.getAllUrl(key);
		return urls == null ? INSTANCE_LOCAL.getAllUrl(key) : urls;
	}
	
	@Override
	public List<String> getAllUrl(String key, boolean subscribe) {
		List<String> urls = INSTANCE_REMOTE.getAllUrl(key,subscribe);
		return urls == null ? INSTANCE_LOCAL.getAllUrl(key,subscribe) : urls;
	}

	@Override
	public boolean connectionFail(String key, String url) {
		if (!INSTANCE_REMOTE.connectionFail(key,url) ) {
			return INSTANCE_LOCAL.connectionFail(key, url);
		}
		return true;
	}
	
	@Override
	public void reset() {
		INSTANCE_REMOTE.reset();
		INSTANCE_LOCAL.reset();
	}
	
	@Override
	public List<String> getRemoteAddress(String serverInfo) {
		return getRemoteAddress(serverInfo);
	}
}
