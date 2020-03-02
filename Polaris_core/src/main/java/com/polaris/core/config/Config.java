package com.polaris.core.config;

import java.util.Properties;

public interface Config {
	public static final String DEFAULT = "default";//map-key
    public static final String SYSTEM = "system";
    public static final String EXT = "ext";
    public static final String GLOBAL = "global";
    
	default Properties getProperties(String file) {return null;}
	default String getProperty(String file, String key) {return null;}
	default void put(String file, Object key, Object value) {}
	default void put(String file, Properties properties) {};
	default boolean contain(Object key) {return false;}
}
