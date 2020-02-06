package com.polaris.core.naming;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.atomic.AtomicBoolean;

import com.polaris.core.OrderWrapper;
import com.polaris.core.util.StringUtil;

@SuppressWarnings("rawtypes")
public class ServerHandlerProvider {
	private static final ServiceLoader<ServerHandler> serviceLoader = ServiceLoader.load(ServerHandler.class);
	private static List<OrderWrapper> discoveryHandlerList = new ArrayList<OrderWrapper>();
	private static volatile AtomicBoolean initialized = new AtomicBoolean(false);
	private static ServerHandler handler = getHandler();
	
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
    private static final ServerHandlerProvider INSTANCE = new ServerHandlerProvider();

    public static ServerHandlerProvider getInstance() {
        return INSTANCE;
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
    
    //获取url
	public String getUrl(String key) {
		List<String> temp = ServerHandlerSupport.getRemoteAddress(key);
		// 单个IP或者多IP不走注册中心
		if (!ServerHandlerSupport.isSkip(temp.get(1))) {
			
			//走注册中心
			if (handler != null) {
				
				
				// 走注册中心
				String url = handler.getUrl(temp.get(1));
				if (StringUtil.isNotEmpty(url)) {
					return temp.get(0) + url + temp.get(2);
				}
			}
		}
		return temp.get(0) + ServerHandlerSupport.getUrl(temp.get(1)) + temp.get(2);
	}

    //获取所有的url
	public List<String> getAllUrl(String key) {
		return getAllUrl(key, true);
	}
	public List<String> getAllUrl(String key, boolean subscribe) {
		List<String> temp = ServerHandlerSupport.getRemoteAddress(key);
		// 单个IP或者多IP不走注册中心
		if (ServerHandlerSupport.isSkip(temp.get(1))) {
			List<String> urlList = new ArrayList<>();
			String[] ips = temp.get(1).split(",");
			for (int i0 = 0; i0 < ips.length; i0++) {
				urlList.add(temp.get(0) + ips[i0] + temp.get(2));
			}
			return urlList;
		}

		// 走注册中心
		if (handler != null) {
			List<String> urls = handler.getAllUrls(temp.get(1));
			for (int i0 = 0; i0 < urls.size(); i0++) {
				String value = temp.get(0) + urls.get(i0) + temp.get(2);
				urls.set(i0, value);
			}
			return urls;			
		}
		return null;
	}

	//获取失败的处理
	public void connectionFail(String key, String url) {
		List<String> temp = ServerHandlerSupport.getRemoteAddress(key);
		List<String> temp2 = ServerHandlerSupport.getRemoteAddress(url);
		// 单个IP或者多IP不走注册中心
		if (!ServerHandlerSupport.isSkip(temp.get(1))) {
			if (handler != null) {
				handler.connectionFail(temp.get(1), temp2.get(1));
				return;
			}
		}
		ServerHandlerSupport.connectionFail(temp.get(1), temp2.get(1));
	}
	
	public static void main(String[] args) {
		String key = "FMS.APIRes.test.mcpsystem.com/api/partner/add";
		System.out.println(ServerHandlerProvider.INSTANCE.getUrl(key));
    }
	
}
