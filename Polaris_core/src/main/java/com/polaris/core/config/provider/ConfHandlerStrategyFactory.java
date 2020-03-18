package com.polaris.core.config.provider;

public abstract class ConfHandlerStrategyFactory {
	
	private static ConfHandlerStrategy strategy = ConfHandlerStrategyDefault.INSTANCE;

	public static ConfHandlerStrategy get() {
		return strategy;
	}
	public static void set(ConfHandlerStrategy configStrategy) {
		strategy = configStrategy;
	}
}
