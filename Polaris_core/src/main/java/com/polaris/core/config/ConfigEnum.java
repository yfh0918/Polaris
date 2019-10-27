package com.polaris.core.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum ConfigEnum {
	
	DEFAULT("default"),
    EXTEND("extend"),
    GLOBAL("global");
    private String type;
    private Map<String, String> cache = new ConcurrentHashMap<>();
	private static final Logger logger = LoggerFactory.getLogger(ConfigEnum.class);

    ConfigEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
    public Map<String, String> getCache() {
        return cache;
    }
    public void put(String key, String value) {
        cache.put(key, value);
        if (logger.isDebugEnabled()) {
			logger.debug("type:{} key:{} value:{} is updated", type,key,value);		
		}
    }
    public String get(String key) {
        return cache.get(key);
    }

}
