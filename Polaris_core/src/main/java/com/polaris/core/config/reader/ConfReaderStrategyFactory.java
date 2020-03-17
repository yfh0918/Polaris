package com.polaris.core.config.reader;

public class ConfReaderStrategyFactory {
	private static ConfReaderStrategy strategy = ConfReaderStrategyDefault.INSTANCE;
	public static ConfReaderStrategy get() {
		return strategy;
	}
	public static void set(ConfReaderStrategy confReaderStrategy) {
		strategy = confReaderStrategy;
	}
}
