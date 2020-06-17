package com.polaris.core.config.provider;

import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import com.polaris.core.config.Config;

public enum ConfigDefault implements Config {
	
	SYS(Type.SYS),
	EXT(Type.EXT),
	GBL(Type.GBL);

	private Type type;
    private Map<String, Properties> cacheFile = new ConcurrentHashMap<>();

    ConfigDefault(Type type) {
        this.type = type;
    }
    
    @Override
    public Type getType() {
        return type;
    }

	@Override
    public void put(String file, Properties properties) {
		cacheFile.put(file, properties);
    }
	
	@Override
    public void put(String file, Object key, Object value) {
		Properties properties = cacheFile.get(file);
		if (properties == null) {
			synchronized(file.intern()) {
				properties = new Properties();
				cacheFile.put(file, properties);
			}
		}
		properties.put(key, value);
    }
    
    @Override
    public Properties getProperties(String file) {
        return cacheFile.get(file);
    }
    
    @Override
    public Collection<Properties> getProperties() {
    	return cacheFile.values();
    }
    
    @Override
    public boolean contain(Object key) {
        for (Properties properties : getProperties()) {
        	if (properties.containsKey(key)) {
        		return true;
        	}
        }
        return false;
    }
    
    @Override
    public boolean contain(String file, Object key) {
    	Properties properties = getProperties(file);
    	if (properties == null) {
    		return false;
    	}
    	return properties.containsKey(key);
    }
    
}
