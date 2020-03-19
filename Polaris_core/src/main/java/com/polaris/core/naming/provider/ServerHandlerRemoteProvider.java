package com.polaris.core.naming.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.atomic.AtomicBoolean;

import com.polaris.core.OrderWrapper;
import com.polaris.core.naming.ServerHandler;
import com.polaris.core.util.StringUtil;

@SuppressWarnings("rawtypes")
public class ServerHandlerRemoteProvider extends ServerHandlerAbsProvider {
	private static final ServiceLoader<ServerHandler> serviceLoader = ServiceLoader.load(ServerHandler.class);
	private static List<OrderWrapper> discoveryHandlerList = new ArrayList<OrderWrapper>();
	private static volatile AtomicBoolean initialized = new AtomicBoolean(false);
	private static ServerHandler handler = getHandler();
    public static final ServerHandlerRemoteProvider INSTANCE = new ServerHandlerRemoteProvider();
    private ServerHandlerRemoteProvider() {}
	
	//初始化
	private static ServerHandler getHandler() {
		if (!initialized.compareAndSet(false, true)) {
            return handler;
        }
    	for (ServerHandler discoveryHandler : serviceLoader) {
    		OrderWrapper.insertSorted(discoveryHandlerList, discoveryHandler);
        }
    	if (discoveryHandlerList.size() > 0) {
    		handler = (ServerHandler)discoveryHandlerList.get(0).getHandler();
    	}
    	return handler;
    }
    
    //注册
    public boolean register(String ip, int port) {
    	if (handler != null) {
    		handler.register(ip, port);
    		return true;
    	}
    	return false;
    }
    
    //反注册
    public boolean deregister(String ip, int port) {
    	if (handler != null) {
			handler.deregister(ip, port);
			return true;
		}
    	return false;
    }
    
    @Override
	public String getUrl(String key) {
		List<String> serverInfoList = parseServer(key);
		if (handler != null) {
			String url = handler.getUrl(serverInfoList.get(1));
			if (StringUtil.isNotEmpty(url)) {
				return serverInfoList.get(0) + url + serverInfoList.get(2);
			}
		}
		return null;
	}

	@Override
	public List<String> getAllUrl(String key) {
		return getAllUrl(key, true);
	}
	
	@Override
	public List<String> getAllUrl(String key, boolean subscribe) {
		List<String> serverInfoList = parseServer(key);
		if (handler != null) {
			List<String> urls = handler.getAllUrls(serverInfoList.get(1),subscribe);
			for (int i0 = 0; i0 < urls.size(); i0++) {
				String value = serverInfoList.get(0) + urls.get(i0) + serverInfoList.get(2);
				urls.set(i0, value);
			}
			return urls;			
		}
		return null;
	}

	@Override
	public boolean connectionFail(String key, String url) {
		List<String> serverInfoList = parseServer(key);
		List<String> serverInfoList2 = parseServer(url);
		// 单个IP或者多IP不走注册中心
		if (handler != null) {
			handler.connectionFail(serverInfoList.get(1), serverInfoList2.get(1));
			return true;
		}
		return false;
	}

	@Override
	protected void reset() {
	}
	
}
