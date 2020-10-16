package com.polaris.core.config.provider;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import com.polaris.core.Constant;
import com.polaris.core.config.ConfigChangeListener;

public class Config implements ConfigChangeListener{
	
    public static Config INSTANCE = new Config();
    
    private Config() {}
    
    public enum Opt {
        ADD,//add
        UPD,//update
        DEL;//delete
    }
    
    public enum Type {
        DEFAULT,
        SYS(),
        EXT();
    }
    
    private Map<String, Properties> cacheFile = new ConcurrentHashMap<>();

    public void put(String group, String file, Object key, Object value) {
	    String mergeKey = merge(group,file);
		Properties properties = cacheFile.get(mergeKey);
		if (properties == null) {
			synchronized(file.intern()) {
				properties = new Properties();
				cacheFile.put(mergeKey, properties);
			}
		}
		if (value != null) {
		    properties.put(key, value);
		} else {
	        properties.remove(key);
		}
    }
    
    
    public Properties getProperties(String group, String file) {
        return cacheFile.get(merge(group,file));
    }
    
    
    @Override
    public void onChange(String sequence, String group, String file, Object key, Object value, Opt opt) {
        
        if (opt != Opt.DEL) {
            put(group, file, key, value);
            putForAll(key.toString(), value.toString());
        } else {
            put(group, file, key, null);
            putForAll(key.toString(), null);
        }
    }

    private Properties cacheForAll = new Properties();
    
    public void putForAll(String key, String value) {
        if (value != null) {
            cacheForAll.put(key, value);
        } else {
            cacheForAll.remove(key);
        }
    }
    
    public String getForAll(String key, String defaultValue) {
        return cacheForAll.getProperty(key, defaultValue);
    }

    private String merge(String group, String fileName) {
        return group + Constant.COLON + fileName;
    }

    public Properties getForAll() {
        return cacheForAll;
    }
}
