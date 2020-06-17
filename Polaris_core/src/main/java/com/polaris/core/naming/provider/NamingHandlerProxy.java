package com.polaris.core.naming.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.atomic.AtomicBoolean;

import com.polaris.core.OrderWrapper;
import com.polaris.core.naming.NamingHandler;
import com.polaris.core.pojo.Server;

@SuppressWarnings("rawtypes")
public class NamingHandlerProxy implements NamingHandler{
	private static final ServiceLoader<NamingHandler> serviceLoader = ServiceLoader.load(NamingHandler.class);
	private static List<OrderWrapper> discoveryHandlerList = new ArrayList<OrderWrapper>();
	private static volatile AtomicBoolean initialized = new AtomicBoolean(false);
	private static NamingHandler handler = getHandler();
    public static final NamingHandlerProxy INSTANCE = new NamingHandlerProxy();
    private NamingHandlerProxy() {}
	
	//初始化
	private static NamingHandler getHandler() {
		if (!initialized.compareAndSet(false, true)) {
            return handler;
        }
    	for (NamingHandler discoveryHandler : serviceLoader) {
    		OrderWrapper.insertSorted(discoveryHandlerList, discoveryHandler);
        }
    	if (discoveryHandlerList.size() > 0) {
    		handler = (NamingHandler)discoveryHandlerList.get(0).getHandler();
    	}
    	return handler;
    }
    
	@Override
    public boolean register(Server server) {
    	if (handler != null) {
    		return handler.register(server);
    	}
    	return false;
    }
    
    @Override
    public boolean deregister(Server server) {
    	if (handler != null) {
    		return handler.deregister(server);
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
