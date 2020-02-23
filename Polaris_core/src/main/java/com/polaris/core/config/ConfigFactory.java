package com.polaris.core.config;

public class ConfigFactory {

	private static Config defaultConfig = ConfEnum.DEFAULT;
	private static Config extendConfig = ConfEnum.EXTEND;
	private static Config globalConfig = ConfEnum.GLOBAL;
	
	public static Config get(String type) {
		if (ConfEnum.EXTEND.getType().equals(type)) {
    		return extendConfig;
    	} else if (ConfEnum.GLOBAL.getType().equals(type)) {
    		return globalConfig;
    	}
    	return defaultConfig;
	}
	public static Config[] get() {
    	return new Config[]{defaultConfig,extendConfig,globalConfig};//第一个必须设置为default
	}
}
