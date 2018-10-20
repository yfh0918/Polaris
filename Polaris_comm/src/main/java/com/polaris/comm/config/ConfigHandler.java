package com.polaris.comm.config;

import java.util.List;

public interface ConfigHandler {
	String getValue(String key, String fileName, boolean isWatch);
	default void addListener(String fileName, ConfListener listener) {
	}
	default List<String> getAllPropertyFiles() {
		return null;
	}
}
