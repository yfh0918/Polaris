package com.polaris.core.config;

public interface ConfHandler {
	default void listen(String fileName, String group, ConfListener listener) {}
	default String get(String fileName, String group) {return null;}
}
