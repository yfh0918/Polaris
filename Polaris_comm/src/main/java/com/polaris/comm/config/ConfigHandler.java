package com.polaris.comm.config;

public interface ConfigHandler {
	String getValue(String key, boolean isWatch);
	default String getFileContent(String fileName) {
		return null;
	}
	default void addListener(String fileName, ConfListener listener) {
		
	}
}
