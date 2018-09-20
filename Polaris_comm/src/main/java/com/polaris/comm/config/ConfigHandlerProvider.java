package com.polaris.comm.config;

import java.util.List;
import java.util.ServiceLoader;

import com.polaris.comm.config.ConfigHandler;

public  class ConfigHandlerProvider {

    private final ServiceLoader<ConfigHandler> serviceLoader = ServiceLoader.load(ConfigHandler.class);
    
    private static final ConfigHandlerProvider INSTANCE = new ConfigHandlerProvider();

    public static ConfigHandlerProvider getInstance() {
        return INSTANCE;
    }

	public String getDataByKey(String key, boolean isWarch) {
		for (ConfigHandler handler : serviceLoader) {
			return handler.getDataByKey(key, isWarch);
		}
		return null;
	}

	public List<String> getAllKeys(String appName) {
		for (ConfigHandler handler : serviceLoader) {
			return handler.getAllKeys(appName);
		}
		return null;
	}
}
