package com.polaris.core.config;

public interface ConfEndPoint {
	default void init() {};
	default void filter(String key, String value) {};
}
