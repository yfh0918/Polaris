package com.polaris.core.config;

public abstract class ConfigStrategyFactory {
	
	private static ConfigStrategy strategy = ConfigStrategyDefault.INSTANCE;

	public static ConfigStrategy get() {
		return strategy;
	}
	public static void set(ConfigStrategy configStrategy) {
		strategy = configStrategy;
	}
}
