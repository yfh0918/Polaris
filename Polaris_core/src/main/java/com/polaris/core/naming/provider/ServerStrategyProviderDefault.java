package com.polaris.core.naming.provider;

import java.util.ArrayList;
import java.util.List;

import com.polaris.core.naming.ServerHandler;
import com.polaris.core.pojo.Server;
import com.polaris.core.pojo.ServerHost;

public class ServerStrategyProviderDefault implements ServerStrategyProvider{
    public static final ServerStrategyProviderDefault INSTANCE = new ServerStrategyProviderDefault();
    private static final ServerHandler INSTANCE_REMOTE = ServerHandlerProvider.INSTANCE;
    private static final ServerHandler INSTANCE_LOCAL = ServerHandlerLocalProvider.INSTANCE;
    private ServerStrategyProviderDefault() {}
    
    @Override
    public boolean register(Server server) {
    	return INSTANCE_REMOTE.register(server);
    }
    
    @Override
    public boolean deregister(Server server) {
    	return INSTANCE_REMOTE.deregister(server);
    }
    
    @Override
	public Server getServer(String serviceName) {
		if (!ServerHost.isIp(serviceName)) {
			return INSTANCE_REMOTE.getServer(serviceName);
		} else {
			return INSTANCE_LOCAL.getServer(serviceName);
		}
	}
    
    @Override
	public List<Server> getServerList(String serviceName) {
		if (!ServerHost.isIp(serviceName)) {
			return INSTANCE_REMOTE.getServerList(serviceName);
		} else {
			return INSTANCE_LOCAL.getServerList(serviceName);
		}
	}
    
    @Override
	public String getRealIpUrl(String serviceNameUrl) {
    	ServerHost serverHost = ServerHost.of(serviceNameUrl);
    	Server server = getServer(serverHost.getServiceName());
		if (server != null) {
			return serverHost.getPrefix() + server.toString()+ serverHost.getUri();
		}
		return null;
	}

    @Override
	public List<String> getRealIpUrlList(String serviceNameUrl) {
    	ServerHost serverHost = ServerHost.of(serviceNameUrl);
    	List<Server> serverList = getServerList(serverHost.getServiceName());
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
	public void onConnectionFail(Server server) {
		INSTANCE_LOCAL.onConnectionFail(server);
	}
}
