package com.polaris.core.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigFactory {

	private static Map<String , Config> configMap = new HashMap<>();
	private static List<Config> configList = new ArrayList<>();
	static {
		configMap.put(Config.SYSTEM, ConfigDefault.SYSTEM);////system plus extent
		configMap.put(Config.GLOBAL, ConfigDefault.GLOBAL);
		configList.add(ConfigDefault.SYSTEM);////system plus extent
		configList.add(ConfigDefault.GLOBAL);
	}
	
	public static Config SYSTEM = get(Config.SYSTEM);
	
	public static Config get(String type) {
		return configMap.get(type);
	}
	public static List<Config> get() {
		return configList;
	}
	public static void set(Config... configs) {
		if (configs == null || configs.length == 0) {
			return;
		}
		configMap.clear();
		configList.clear();
		if (configs.length == 1) {
			configMap.put(Config.SYSTEM, configs[0]);
			configMap.put(Config.GLOBAL, configs[0]);
			configList.add(configs[0]);
			return;
		}
		if (configs.length >= 2) {
			configMap.put(Config.SYSTEM, configs[0]);
			configMap.put(Config.GLOBAL, configs[1]);
			configList.add(configs[0]);
			configList.add(configs[1]);
			return;
		}
	}
}
