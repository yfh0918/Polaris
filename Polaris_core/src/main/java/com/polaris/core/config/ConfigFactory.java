package com.polaris.core.config;

public class ConfigFactory {

	private static Config defaultConfig = ConfEnum.DEFAULT;
	private static Config extendConfig = ConfEnum.EXTEND;
	private static Config globalConfig = ConfEnum.GLOBAL;
	
	public static Config DEFAULT = get(Config.DEFAULT);
	
	public static Config get(String type) {
		if (Config.EXTEND.equals(type)) {
    		return extendConfig != null ? extendConfig : defaultConfig;
    	} else if (Config.GLOBAL.equals(type)) {
    		return globalConfig != null ? globalConfig : defaultConfig;
    	}
    	return defaultConfig;
	}
	public static Config[] get() {
		if (extendConfig == null) {
			return new Config[]{defaultConfig};
		} else if (globalConfig == null) {
			return new Config[]{defaultConfig,extendConfig};
		}
    	return new Config[]{defaultConfig,extendConfig,globalConfig};//第一个必须设置为default
	}
	public static void set(Config... configs) {
		if (configs == null || configs.length == 0) {
			throw new RuntimeException("config's number can't 1 than smaller ");
		}
		if (configs.length == 1) {
			defaultConfig = configs[0];
			extendConfig = null;
			globalConfig = null;
			return;
		}
		if (configs.length == 2) {
			defaultConfig = configs[0];
			extendConfig = configs[1];
			globalConfig = null;
			return;
		}
		if (configs.length == 3) {
			defaultConfig = configs[0];
			extendConfig = configs[1];
			globalConfig = configs[2];
			return;
		}
		if (configs.length > 3) {
			throw new RuntimeException("config's number can't 3 than bigger ");
		}
	}
}
