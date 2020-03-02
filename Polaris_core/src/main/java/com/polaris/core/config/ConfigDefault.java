package com.polaris.core.config;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum ConfigDefault implements Config {
	
	SYSTEM(Config.SYSTEM),
	EXT(Config.EXT),
    GLOBAL(Config.GLOBAL);

	private String type;
    private Map<String, Properties> cacheFile = new ConcurrentHashMap<>();
	private static final Logger logger = LoggerFactory.getLogger(ConfigDefault.class);

    ConfigDefault(String type) {
        this.type = type;
    }

    protected String getType() {
        return type;
    }

	@Override
    public void put(String file, Properties properties) {
		cacheFile.put(file, properties);
        if (logger.isDebugEnabled()) {
        	for (Map.Entry<Object,Object> entry : properties.entrySet()) {
        		logger.debug("type:{} file:{}, key:{} value:{} is updated", type,file,entry.getKey(),entry.getValue());
        	}
		}

    }
    
    @Override
    public void put(String file, Object key, Object value) {
    	
    	//载入缓存
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
    public Properties getProperties(String file) {
        return cacheFile.get(file);
    }
    
    @Override
    public String getProperty(String file, String key) {
        return cacheFile.get(file).getProperty(key);
    }
    
    @Override
    public boolean contain(Object key) {
        for (Properties properties : cacheFile.values()) {
        	if (properties.containsKey(key)) {
        		return true;
        	}
        }
        return false;
    }
    
}
