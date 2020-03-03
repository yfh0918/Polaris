package com.polaris.core.config;

import java.util.Collection;
import java.util.Properties;

public interface Config {
	public static final String DEFAULT = "default";//map-key
    public static final String SYSTEM = "system";
    public static final String EXT = "ext";
    public static final String GLOBAL = "global";
    
	default Collection<Properties> getProperties() {return null;}
	default Properties getProperties(String file) {return null;}
	default void put(String file, Properties properties) {};
	default boolean contain(Object key) {return false;}
}
