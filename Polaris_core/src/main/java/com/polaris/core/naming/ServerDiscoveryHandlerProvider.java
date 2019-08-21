package com.polaris.core.naming;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import com.polaris.core.util.StringUtil;

public class ServerDiscoveryHandlerProvider {
	private final ServiceLoader<ServerDiscoveryHandler> serviceLoader = ServiceLoader.load(ServerDiscoveryHandler.class);

    private static final ServerDiscoveryHandlerProvider INSTANCE = new ServerDiscoveryHandlerProvider();

    public static ServerDiscoveryHandlerProvider getInstance() {
        return INSTANCE;
    }
    
    //注册
    public void register(String ip, int port) {
    	for (ServerDiscoveryHandler handler : serviceLoader) {
			handler.register(ip, port);
		}
    }
    
    //反注册
    public void deregister(String ip, int port) {
    	for (ServerDiscoveryHandler handler : serviceLoader) {
			handler.deregister(ip, port);
		}
    }
    
    //获取url
	public String getUrl(String key) {
		List<String> temp = ServerDiscoveryHandlerSupport.getRemoteAddress(key);
		// 单个IP或者多IP不走注册中心
		if (!ServerDiscoveryHandlerSupport.isSkip(temp.get(1))) {
			
			//走注册中心
			for (ServerDiscoveryHandler handler : serviceLoader) {
				
				
				// 走注册中心
				String url = handler.getUrl(temp.get(1));
				if (StringUtil.isNotEmpty(url)) {
					return temp.get(0) + url + temp.get(2);
				}
				
				//只走第一个注册中心
				break;
			}
		}
		return temp.get(0) + ServerDiscoveryHandlerSupport.getUrl(temp.get(1)) + temp.get(2);
	}

    //获取所有的url
	public List<String> getAllUrl(String key) {
		
		List<String> temp = ServerDiscoveryHandlerSupport.getRemoteAddress(key);
		// 单个IP或者多IP不走注册中心
		if (ServerDiscoveryHandlerSupport.isSkip(temp.get(1))) {
			List<String> urlList = new ArrayList<>();
			String[] ips = temp.get(1).split(",");
			for (int i0 = 0; i0 < ips.length; i0++) {
				urlList.add(temp.get(0) + ips[i0] + temp.get(2));
			}
			return urlList;
		}

		// 走注册中心
		for (ServerDiscoveryHandler handler : serviceLoader) {
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
		List<String> temp = ServerDiscoveryHandlerSupport.getRemoteAddress(key);
		List<String> temp2 = ServerDiscoveryHandlerSupport.getRemoteAddress(url);
		// 单个IP或者多IP不走注册中心
		if (!ServerDiscoveryHandlerSupport.isSkip(temp.get(1))) {
			for (ServerDiscoveryHandler handler : serviceLoader) {
				handler.connectionFail(temp.get(1), temp2.get(1));
				return;
			}
		}
		ServerDiscoveryHandlerSupport.connectionFail(temp.get(1), temp2.get(1));
	}
	
	public static void main(String[] args) {
		String key = "FMS.APIRes.test.mcpsystem.com/api/partner/add";
		System.out.println(ServerDiscoveryHandlerProvider.INSTANCE.getUrl(key));
    }
}
