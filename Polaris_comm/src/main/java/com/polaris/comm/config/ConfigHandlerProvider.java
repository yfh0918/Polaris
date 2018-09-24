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

	public List<String> getAllNameSpaces(boolean isWatch) {
		for (ConfigHandler handler : serviceLoader) {
			return handler.getAllNameSpaces(isWatch);
		}
		return null;
	}
	public boolean addNameSpace(String namespace, boolean isWatch) {
		for (ConfigHandler handler : serviceLoader) {
			return handler.addNameSpace(namespace, isWatch);
		}
		return false;
	}
	public boolean deleteNameSpace(String namespace, boolean isWatch) {
		for (ConfigHandler handler : serviceLoader) {
			return handler.deleteNameSpace(namespace, isWatch);
		}
		return false;
	}

	
	public List<String> getAllGroups(String nameSpace, boolean isWatch) {
		for (ConfigHandler handler : serviceLoader) {
			return handler.getAllGroups(nameSpace, isWatch);
		}
		return null;
	}
	public boolean addGroup(String nameSpace, String group, boolean isWatch) {
		for (ConfigHandler handler : serviceLoader) {
			return handler.addGroup(nameSpace, group, isWatch);
		}
		return false;
	}
	public boolean deleteGroup(String nameSpace, String group, boolean isWatch) {
		for (ConfigHandler handler : serviceLoader) {
			return handler.deleteGroup(nameSpace, group, isWatch);
		}
		return false;
	}

	
	public String getKey(String nameSpace, String group, String key, boolean isWatch) {
		for (ConfigHandler handler : serviceLoader) {
			return handler.getKey(nameSpace, group, key, isWatch);
		}
		return null;
	}

	public List<String> getAllKeys(String nameSpace, String group, boolean isWatch) {
		for (ConfigHandler handler : serviceLoader) {
			return handler.getAllKeys(nameSpace, group, isWatch);
		}
		return null;
	}
	
	public boolean deleteKey(String nameSpace, String group, String key, boolean isWatch) {
		for (ConfigHandler handler : serviceLoader) {
			return handler.deleteKey(nameSpace, group, key , isWatch);
		}
		return false;
	}

	public boolean addKey(String nameSpace, String group, String key, String data, boolean isWatch) {
		for (ConfigHandler handler : serviceLoader) {
			return handler.addKey(nameSpace, group, key, data, isWatch);
		}
		return false;
	}
}
