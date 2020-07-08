package com.polaris.core.config.provider;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.polaris.core.config.ConfHandler;
import com.polaris.core.config.ConfigChangeListener;
import com.polaris.core.config.Config.Type;

public class ConfHandlerFactory {
	
	private static Map<Type , ConfHandler> confHandlerProxyMap = new ConcurrentHashMap<>();
	public static ConfHandler getOrCreate(Type type, ConfigChangeListener... configListeners) {
	    ConfHandler confHandlerProxy = confHandlerProxyMap.get(type);
		if (confHandlerProxy == null) {
			synchronized(type.name().intern()) {
			    if (type == Type.SYS) {
	                confHandlerProxy = new ConfHandlerSystem(type,configListeners);
			    } else {
	                confHandlerProxy = new ConfHandlerProxy(type,configListeners);
			    }
			    confHandlerProxyMap.put(type, confHandlerProxy);
			}
		}
		return confHandlerProxy;
		
	}
}
