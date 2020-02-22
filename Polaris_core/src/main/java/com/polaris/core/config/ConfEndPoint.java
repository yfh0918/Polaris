package com.polaris.core.config;

public interface ConfEndPoint {
	default void init() {};
	default void loadExtend(ConfHandlerEnum configEnum) {};
	default void loadGlobal(ConfHandlerEnum configEnum) {};
	default void filter(String key, String value) {};
}
