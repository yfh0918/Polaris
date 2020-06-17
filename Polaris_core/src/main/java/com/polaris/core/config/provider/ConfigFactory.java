package com.polaris.core.config.provider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.polaris.core.config.Config;
import com.polaris.core.config.Config.Type;

public class ConfigFactory {

	private static Map<Type , Config> configMap = new HashMap<>();
	private static List<Config> configList = new ArrayList<>();
	static {
		configMap.put(Type.SYS, ConfigDefault.SYS);
		configMap.put(Type.EXT, ConfigDefault.EXT);
		configMap.put(Type.GBL, ConfigDefault.GBL);
		configList.add(ConfigDefault.SYS);
		configList.add(ConfigDefault.EXT);
		configList.add(ConfigDefault.GBL);
	}
	
	public static Config get(Type type) {
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
			configMap.put(Type.SYS, configs[0]);
			configMap.put(Type.EXT, configs[0]);
			configMap.put(Type.GBL, configs[0]);
			configList.add(configs[0]);
			return;
		}
		if (configs.length == 2) {
			configMap.put(Type.SYS, configs[0]);
			configMap.put(Type.EXT, configs[1]);
			configMap.put(Type.GBL, configs[1]);
			configList.add(configs[0]);
			configList.add(configs[1]);
			return;
		}
		if (configs.length >= 3) {
			configMap.put(Type.SYS, configs[0]);
			configMap.put(Type.EXT, configs[1]);
			configMap.put(Type.GBL, configs[2]);
			configList.add(configs[0]);
			configList.add(configs[1]);
			configList.add(configs[2]);
			return;
		}
	}
}
