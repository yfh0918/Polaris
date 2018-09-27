package com.polaris.comm.config;

import java.util.ServiceLoader;

public  class ConfigHandlerProvider {

    private final ServiceLoader<ConfigHandler> serviceLoader = ServiceLoader.load(ConfigHandler.class);
    
    private static final ConfigHandlerProvider INSTANCE = new ConfigHandlerProvider();

    public static ConfigHandlerProvider getInstance() {
        return INSTANCE;
    }

	public String getKey(String key, boolean isWatch) {
		for (ConfigHandler handler : serviceLoader) {
			return handler.getKey(ConfClient.getEnv(), ConfClient.getNameSpace(), ConfClient.getCluster(), ConfClient.getAppName(), key, isWatch);
		}
		return null;
	}
}
