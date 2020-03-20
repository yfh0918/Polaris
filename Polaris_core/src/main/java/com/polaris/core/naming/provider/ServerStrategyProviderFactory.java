package com.polaris.core.naming.provider;

public class ServerStrategyProviderFactory {
	private static ServerStrategyProvider strategy = ServerStrategyProviderDefault.INSTANCE;

	public static ServerStrategyProvider get() {
		return strategy;
	}
	public static void set(ServerStrategyProvider serverStrategy) {
		strategy = serverStrategy;
	}
}
