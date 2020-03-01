package com.polaris.core.config;

import java.util.Properties;

public interface Config {
    public static final String DEFAULT = "default";
    public static final String EXTEND = "extend";
    public static final String GLOBAL = "global";
    
	default Properties get() {return null;}
	default Properties get(String file) {return null;}
	default String get(String file, String key) {return null;}
	default void put(String file, String key, String value) {}
	default void put(String file, Properties properties) {};
}
