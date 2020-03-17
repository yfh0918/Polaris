package com.polaris.core.config;

public abstract class ConfigStrategyFactory {

	public static ConfigStrategy get() {
		return ConfigStrategyDefault.INSTANCE;
	}
}
