package com.polaris.core.config;

import java.util.Map;

public interface Config {
    public static final String DEFAULT = "default";
    public static final String EXTEND = "extend";
    public static final String GLOBAL = "global";
    
	default Map<String, String> get() {return null;}
	default String get(String key) {return null;}
	default void put(String key, String value) {}
}
