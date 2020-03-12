package com.polaris.core.config;

import java.util.Collection;
import java.util.Properties;

public interface Config {
	
	public enum Opt {
		ADD,
		UPDATE,
	    DELETE;
	}
	
    public static final String SYSTEM = "system";
    public static final String EXT = "ext";
    public static final String GLOBAL = "global";
    
    String getType();
	default Collection<Properties> getProperties() {return null;}
	default Properties getProperties(String file) {return null;}
	default void put(String file, Properties properties) {};
	default void put(String file, Object key, Object value) {};
	default boolean contain(String file, Object key){return false;}
	default boolean contain(Object key) {return false;}
}
