package com.polaris.core.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.core.Constant;
import com.polaris.core.util.StringUtil;

public enum ConfEnum implements Config{
	
	DEFAULT(Config.DEFAULT),
    EXTEND(Config.EXTEND),
    GLOBAL(Config.GLOBAL);
    private String type;
    private Map<String, String> cache = new ConcurrentHashMap<>();
	private static final Logger logger = LoggerFactory.getLogger(ConfEnum.class);

    ConfEnum(String type) {
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
    
    @Override
    public void put(String config) {
    	if (StringUtil.isNotEmpty(config)) {
			String[] contents = config.split(Constant.LINE_SEP);
			for (String content : contents) {
				String[] keyvalue = ConfHandlerSupport.getKeyValue(content);
				if (keyvalue != null) {
					cache.put(keyvalue[0], ConfHandlerSupport.getDecryptValue(keyvalue[1]));
				}
			}
		} 
    }
}
