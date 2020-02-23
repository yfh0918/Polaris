package com.polaris.core.config;

public class ConfigFactory {

	private static Config defaultConfig = ConfEnum.DEFAULT;
	private static Config extendConfig = ConfEnum.EXTEND;
	private static Config globalConfig = ConfEnum.GLOBAL;
	
	public static Config get(String type) {
		if (type.equals(ConfEnum.EXTEND.getType())) {
    		return extendConfig;
    	}
    	if (type.equals(ConfEnum.GLOBAL.getType())) {
    		return globalConfig;
    	}
    	return defaultConfig;
	}
	public static Config get() {
    	return defaultConfig;
	}
}
