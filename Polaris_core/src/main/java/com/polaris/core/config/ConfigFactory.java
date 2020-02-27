package com.polaris.core.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigFactory {

	private static Map<String , Config> configMap = new HashMap<>();
	private static List<Config> configList = new ArrayList<>();
	static {
		configMap.put(Config.DEFAULT, ConfigDefaultImpl.DEFAULT);
		configMap.put(Config.EXTEND, ConfigDefaultImpl.EXTEND);
		configMap.put(Config.GLOBAL, ConfigDefaultImpl.GLOBAL);
		configList.add(ConfigDefaultImpl.DEFAULT);
		configList.add(ConfigDefaultImpl.EXTEND);
		configList.add(ConfigDefaultImpl.GLOBAL);
	}
	
	public static Config DEFAULT = get(Config.DEFAULT);
	
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
			configMap.put(Config.DEFAULT, configs[0]);
			configMap.put(Config.EXTEND, configs[0]);
			configMap.put(Config.GLOBAL, configs[0]);
			configList.add(configs[0]);
			return;
		}
		if (configs.length == 2) {
			configMap.put(Config.DEFAULT, configs[0]);
			configMap.put(Config.EXTEND, configs[1]);
			configMap.put(Config.GLOBAL, configs[1]);
			configList.add(configs[0]);
			configList.add(configs[1]);
			return;
		}
		if (configs.length >= 3) {
			configMap.put(Config.DEFAULT, configs[0]);
			configMap.put(Config.EXTEND, configs[1]);
			configMap.put(Config.GLOBAL, configs[2]);
			configList.add(configs[0]);
			configList.add(configs[1]);
			configList.add(configs[2]);
			return;
		}
	}
}
