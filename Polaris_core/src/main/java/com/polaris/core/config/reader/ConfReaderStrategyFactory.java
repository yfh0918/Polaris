package com.polaris.core.config.reader;

public class ConfReaderStrategyFactory {

	public static ConfReaderStrategy get() {
		return ConfReaderStrategyDefault.INSTANCE;
	}
}
