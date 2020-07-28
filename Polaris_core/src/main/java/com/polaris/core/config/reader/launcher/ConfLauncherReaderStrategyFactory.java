package com.polaris.core.config.reader.launcher;

public class ConfLauncherReaderStrategyFactory {
	private static ConfLauncherReaderStrategy strategy = ConfLauncherReaderStrategyDefault.INSTANCE;
	public static ConfLauncherReaderStrategy get() {
		return strategy;
	}
	public static void set(ConfLauncherReaderStrategy confReaderStrategy) {
		strategy = confReaderStrategy;
	}
}
