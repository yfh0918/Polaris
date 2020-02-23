package com.polaris.core.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.core.Constant;
import com.polaris.core.util.StringUtil;

public enum ConfHandlerEnum {
	
	DEFAULT(ConfHandlerProviderAbs.DEFAULT),
    EXTEND(ConfHandlerProviderAbs.EXTEND),
    GLOBAL(ConfHandlerProviderAbs.GLOBAL);
    private String type;
    private Map<String, String> cache = new ConcurrentHashMap<>();
	private static final Logger logger = LoggerFactory.getLogger(ConfHandlerEnum.class);

    ConfHandlerEnum(String type) {
        this.type = type;
    }

    protected String getType() {
        return type;
    }
    protected Map<String, String> get() {
        return cache;
    }
    protected void put(String key, String value) {
    	
    	//载入缓存
        cache.put(key, value);
        if (logger.isDebugEnabled()) {
			logger.debug("type:{} key:{} value:{} is updated", type,key,value);		
		}
        
        //外部模块接入点的filter
        ConfHandlerProvider.INSTANCE.filterEndPoint(key, value);
    }
    protected String get(String key) {
        return cache.get(key);
    }
    
    protected void put(String config) {
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
    
    public static ConfHandlerEnum getConfig(String type) {
    	if (type.equals(ConfHandlerEnum.EXTEND.type)) {
    		return ConfHandlerEnum.EXTEND;
    	}
    	if (type.equals(ConfHandlerEnum.GLOBAL.type)) {
    		return ConfHandlerEnum.GLOBAL;
    	}
    	return ConfHandlerEnum.DEFAULT;
    }

}
