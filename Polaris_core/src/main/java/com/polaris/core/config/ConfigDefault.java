package com.polaris.core.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum ConfigDefault implements Config {
	
	DEFAULT(Config.DEFAULT),
    EXTEND(Config.EXTEND),
    GLOBAL(Config.GLOBAL);
    private String type;
    private Properties cacheAll = new Properties();
    private Map<String, Properties> cacheFile = new HashMap<>();
	private static final Logger logger = LoggerFactory.getLogger(ConfigDefault.class);

    ConfigDefault(String type) {
        this.type = type;
    }

    protected String getType() {
        return type;
    }

    
	@Override
    public void put(String file, Properties properties) {
		cacheAll.putAll(properties);
		cacheFile.put(file, properties);
    }
    
    
    @Override
    public void put(String file, String key, String value) {
    	
    	//载入缓存
    	cacheAll.put(key, value);
    	Properties cache = cacheFile.get(file);
    	if (cache == null) {
    		synchronized(file.intern()) {
    			cache = cacheFile.get(file);
    			if (cache == null) {
    				cache = new Properties();
    				cacheFile.put(file, cache);
    			}
    		}
    	}
    	cache.put(key, value);
        if (logger.isDebugEnabled()) {
			logger.debug("type:{} file:{}, key:{} value:{} is updated", type,file,key,value);		
		}
        
    }
    
    @Override
    public Properties get() {
        return cacheAll;
    }
    
    @Override
    public Properties get(String file) {
        return cacheFile.get(file);
    }
    
    @Override
    public String get(String file, String key) {
        return cacheFile.get(file).getProperty(key);
    }  
    
}
