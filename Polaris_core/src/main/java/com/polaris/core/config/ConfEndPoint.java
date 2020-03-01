package com.polaris.core.config;

public interface ConfEndPoint {
	default void init() {};
	default void filter(String file, String key, String value) {};
}
