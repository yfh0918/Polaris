package com.polaris.core.config;

import java.util.Properties;

public interface Config {
    public static final String SYSTEM = "system";
    public static final String GLOBAL = "global";
    
	default Properties getProperties() {return null;}
	default Properties getProperties(String file) {return null;}
	default String getProperty(String key) {return null;}
	default String getProperty(String file, String key) {return null;}
	default void put(String key, String value) {}
	default void put(Properties properties) {}
	default void put(String file, String key, String value) {}
	default void put(String file, Properties properties) {};
}
