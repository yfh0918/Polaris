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

	public String getDataByKey(String nameSpace, String group, String key, boolean isWatch) {
		for (ConfigHandler handler : serviceLoader) {
			return handler.getDataByKey(nameSpace, group, key, isWatch);
		}
		return null;
	}

	public List<String> getAllKeys(String nameSpace, String group) {
		for (ConfigHandler handler : serviceLoader) {
			return handler.getAllKeys(nameSpace, group);
		}
		return null;
	}
	
	public boolean deleteDataByKey(String nameSpace, String group, String key, boolean isWatch) {
		for (ConfigHandler handler : serviceLoader) {
			return handler.deleteDataByKey(nameSpace, group, key , isWatch);
		}
		return false;
	}

	public boolean setDataByKey(String nameSpace, String group, String key, String data, boolean isWatch) {
		for (ConfigHandler handler : serviceLoader) {
			return handler.setDataByKey(nameSpace, group, key, data, isWatch);
		}
		return false;
	}
}
