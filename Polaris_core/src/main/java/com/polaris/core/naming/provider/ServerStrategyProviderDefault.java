package com.polaris.core.naming.provider;

import java.util.ArrayList;
import java.util.List;

import com.polaris.core.naming.ServerHandler;
import com.polaris.core.naming.ServerHandlerLocal;
import com.polaris.core.pojo.Server;
import com.polaris.core.pojo.ServerHost;

public class ServerStrategyProviderDefault implements ServerStrategyProvider{
    public static final ServerStrategyProviderDefault INSTANCE = new ServerStrategyProviderDefault();
    private static final ServerHandler INSTANCE_REMOTE = ServerHandlerProvider.INSTANCE;
    private static final ServerHandlerLocal INSTANCE_LOCAL = ServerHandlerLocalProvider.INSTANCE;
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
	public String getRealIpUrl(String serviceNameUrl) {
    	ServerHost serverHost = ServerHost.of(serviceNameUrl);
    	Server server = null;
		if (!ServerHost.isIp(serverHost)) {
			server = INSTANCE_REMOTE.getServer(serverHost.getServiceName());
		} else {
			server = INSTANCE_LOCAL.getServer(serverHost.getServiceName());
		}
		if (server != null) {
			return serverHost.getPrefix() + server.toString()+ serverHost.getUri();
		}
		return null;
	}

    @Override
	public List<String> getRealIpUrlList(String serviceNameUrl) {
    	ServerHost serverHost = ServerHost.of(serviceNameUrl);
    	List<Server> serverList = null;
		if (!ServerHost.isIp(serverHost)) {
			serverList = INSTANCE_REMOTE.getServerList(serverHost.getServiceName());
		} else {
			serverList = INSTANCE_LOCAL.getServerList(serverHost.getServiceName());
		}
		if (serverList != null && serverList.size() > 0) {
			List<String> urlList = new ArrayList<>();
			for (Server server : serverList) {
				urlList.add(serverHost.getPrefix()+server.toString()+serverHost.getUri());
			}
			return urlList;
		}
		return null;
	}
	
    @Override
	public void connectionFail(String serviceNameUrl, String realIpUrl) {
    	//example serviceNameUrl=http://192.168.1.1:8081,192.168.2.2:8081/cc/ip
    	//example realIpUrl=http://192.168.1.1:8081/cc/ip
    	ServerHost serverHost = ServerHost.of(serviceNameUrl);
		if (ServerHost.isIp(serverHost)) {
	    	String serviceName = serverHost.getServiceName();
			INSTANCE_LOCAL.connectionFail(
					serviceName, 
					INSTANCE_LOCAL.getServer(ServerHost.of(realIpUrl).getServiceName()));
		} 
	}
	
    @Override
	public void init() {
		INSTANCE_LOCAL.init();
	}
}
