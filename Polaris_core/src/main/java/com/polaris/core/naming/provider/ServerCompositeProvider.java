package com.polaris.core.naming.provider;

import java.util.List;

public class ServerCompositeProvider {
    public static final ServerCompositeProvider INSTANCE = new ServerCompositeProvider();
    private static final ServerHandlerRemoteProvider INSTANCE_REMOTE = ServerHandlerRemoteProvider.INSTANCE;
    private static final ServerHandlerLocalProvider INSTANCE_LOCAL = ServerHandlerLocalProvider.INSTANCE;
    private ServerCompositeProvider() {}
    
    //注册
    public boolean register(String ip, int port) {
    	return INSTANCE_REMOTE.register(ip, port);
    }
    
    //反注册
    public boolean deregister(String ip, int port) {
    	return INSTANCE_REMOTE.deregister(ip, port);
    }
    
    //获取url
	public String getUrl(String key) {
		String url = INSTANCE_REMOTE.getUrl(key);
		return url == null ? INSTANCE_LOCAL.getUrl(key) : url;
	}

    //获取所有的url
	public List<String> getAllUrl(String key) {
		List<String> urls = INSTANCE_REMOTE.getAllUrl(key);
		return urls == null ? INSTANCE_LOCAL.getAllUrl(key) : urls;
	}
	
	public List<String> getAllUrl(String key, boolean subscribe) {
		List<String> urls = INSTANCE_REMOTE.getAllUrl(key,subscribe);
		return urls == null ? INSTANCE_LOCAL.getAllUrl(key,subscribe) : urls;
	}

	//获取失败的处理
	public void connectionFail(String key, String url) {
		if (!INSTANCE_REMOTE.connectionFail(key,url) ) {
			INSTANCE_LOCAL.connectionFail(key, url);
		}
	}
	
	public void reset() {
		INSTANCE_REMOTE.reset();
		INSTANCE_LOCAL.reset();
	}
	
	public List<String> getRemoteAddress(String serverInfo) {
		return INSTANCE_REMOTE.getRemoteAddress(serverInfo);
	}
	
	public static void main(String[] args) {
		String key = "http://localhost:8090/api/partner/add";
		System.out.println(ServerCompositeProvider.INSTANCE.getAllUrl(key));
    }
	
}
