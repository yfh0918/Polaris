package com.polaris.core.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum ConfigDefault implements Config{
	
	DEFAULT(Config.DEFAULT),
    EXTEND(Config.EXTEND),
    GLOBAL(Config.GLOBAL);
    private String type;
    private Map<String, String> cache = new ConcurrentHashMap<>();
	private static final Logger logger = LoggerFactory.getLogger(ConfigDefault.class);

    ConfigDefault(String type) {
        this.type = type;
    }

    protected String getType() {
        return type;
    }
    
    @Override
    public Map<String, String> get() {
        return cache;
    }
    
    @Override
    public void put(String key, String value) {
    	
    	//载入缓存
        cache.put(key, value);
        if (logger.isDebugEnabled()) {
			logger.debug("type:{} key:{} value:{} is updated", type,key,value);		
		}
        
    }
    
    @Override
    public String get(String key) {
        return cache.get(key);
    }
    
    
}
