package com.polaris.core.config.provider;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.polaris.core.config.Config.Type;

public class ConfHandlerProviderFactory {
	
	private static Map<Type , ConfHandlerProvider> confHandlerProviderMap = new ConcurrentHashMap<>();
	public static ConfHandlerProvider get(Type type) {
		ConfHandlerProvider provider = confHandlerProviderMap.get(type);
		if (provider == null) {
			synchronized(type.name().intern()) {
				if (type.equals(Type.EXT)) {
					provider = ConfHandlerExtProvider.INSTANCE;
				} else if (type.equals(Type.GBL)) {
					provider = ConfHandlerGlobalProvider.INSTANCE;
				} else if (type.equals(Type.SYS)) {
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
