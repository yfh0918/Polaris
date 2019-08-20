package com.polaris.core.config;

public interface ConfigHandler {
	default void addListener(String fileName, String group, ConfListener listener) {
	}
	default String getConfig(String fileName, String group) {
		return null;
	}
	default String getValue(String key, String fileName) {
		return null;
	}
}
