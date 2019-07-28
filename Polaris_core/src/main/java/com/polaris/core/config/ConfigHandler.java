package com.polaris.core.config;

public interface ConfigHandler {
	String getValue(String key, String fileName, boolean isWatch);
	default void addListener(String fileName, ConfListener listener) {
	}
	default String getConfig(String fileName) {
		return null;
	}
}
