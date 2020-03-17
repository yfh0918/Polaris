package com.polaris.core.config.provider;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.polaris.core.config.Config;

public class ConfHandlerProviderFactory {
	
	private static Map<String , ConfHandlerProvider> confHandlerProviderMap = new ConcurrentHashMap<>();
	public static ConfHandlerProvider get(String type) {
		ConfHandlerProvider provider = confHandlerProviderMap.get(type);
		if (provider == null) {
			synchronized(type.intern()) {
				if (type.equals(Config.EXT)) {
					provider = ConfHandlerExtProvider.INSTANCE;
				} else if (type.equals(Config.GLOBAL)) {
					provider = ConfHandlerGlobalProvider.INSTANCE;
				} else if (type.equals(Config.SYSTEM)) {
					provider = ConfHandlerSysProvider.INSTANCE;
				} else {
					throw new RuntimeException("type:"+type+"is incorrect");
				}
				confHandlerProviderMap.put(type, provider);
			}
		}
		return provider;
		
	}
}
