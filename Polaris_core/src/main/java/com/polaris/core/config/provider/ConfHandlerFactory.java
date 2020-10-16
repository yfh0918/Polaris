package com.polaris.core.config.provider;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.polaris.core.config.ConfHandler;
import com.polaris.core.config.ConfigChangeListener;
import com.polaris.core.config.provider.Config.Type;

public class ConfHandlerFactory {
	
	private static Map<Type , ConfHandler> confHandlerMap = new ConcurrentHashMap<>();
	public static void create(Type type, ConfigChangeListener... configListeners) {
	    ConfHandler confHandler = confHandlerMap.get(type);
		if (confHandler == null) {
			synchronized(type.name().intern()) {
			    if (type == Type.SYS) {
			        confHandler = new ConfHandlerSystem(configListeners);
			    } else if (type == Type.EXT) {
			        confHandler = new ConfHandlerExtension(configListeners);
			    } else {
			        confHandler = new ConfHandlerDefault();
			    }
			    confHandlerMap.put(type, confHandler);
			}
		}
	}

	public static ConfHandler get(Type type) {
        return confHandlerMap.get(type);
    }
}
