package com.polaris.core.config.provider;

import java.util.HashMap;
import java.util.Map;

import com.polaris.core.config.Config;
import com.polaris.core.config.Config.Type;

public class ConfigFactory {

	private static Map<Type , Config> configMap = new HashMap<>();
	static {
		configMap.put(Type.SYS, new ConfigDefault());
		configMap.put(Type.EXT, new ConfigDefault());
	}
	
	public static Config get(Type type) {
		return configMap.get(type);
	}
}
