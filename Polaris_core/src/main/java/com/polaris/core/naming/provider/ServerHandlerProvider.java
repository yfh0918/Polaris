package com.polaris.core.naming.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.atomic.AtomicBoolean;

import com.polaris.core.OrderWrapper;
import com.polaris.core.naming.ServerHandler;
import com.polaris.core.pojo.Server;

@SuppressWarnings("rawtypes")
public class ServerHandlerProvider implements ServerHandler{
	private static final ServiceLoader<ServerHandler> serviceLoader = ServiceLoader.load(ServerHandler.class);
	private static List<OrderWrapper> discoveryHandlerList = new ArrayList<OrderWrapper>();
	private static volatile AtomicBoolean initialized = new AtomicBoolean(false);
	private static ServerHandler handler = getHandler();
    public static final ServerHandlerProvider INSTANCE = new ServerHandlerProvider();
    private ServerHandlerProvider() {}
	
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
    
	@Override
    public boolean register(String ip, int port) {
    	if (handler != null) {
    		return handler.register(ip, port);
    	}
    	return false;
    }
    
    @Override
    public boolean deregister(String ip, int port) {
    	if (handler != null) {
    		return handler.deregister(ip, port);
		}
    	return false;
    }
    
    @Override
	public Server getServer(String serviceName) {
		if (handler != null) {
			return handler.getServer(serviceName);
		}
		return null;
	}

	@Override
	public List<Server> getServerList(String serviceName) {
		if (handler != null) {
			return handler.getServerList(serviceName);
		}
		return null;
	}

}
