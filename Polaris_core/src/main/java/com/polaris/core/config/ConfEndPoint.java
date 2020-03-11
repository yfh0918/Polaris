package com.polaris.core.config;

public interface ConfEndPoint {
	default void init() {};
	default void put(String type, String file, String key, String value) {};
}
