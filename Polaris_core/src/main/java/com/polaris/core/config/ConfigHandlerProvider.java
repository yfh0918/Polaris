package com.polaris.core.config;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.pagehelper.util.StringUtil;
import com.polaris.core.Constant;
import com.polaris.core.config.value.AutoUpdateConfigChangeListener;
import com.polaris.core.util.SpringUtil;

public abstract class ConfigHandlerProvider {

	private static final Logger logger = LoggerFactory.getLogger(ConfigHandlerProvider.class);
	
    private static final ServiceLoader<ConfigHandler> serviceLoader = ServiceLoader.load(ConfigHandler.class);
    
	private static volatile Map<String, Map<String, String>> cacheFileMap = new ConcurrentHashMap<>();
    
    // 载入文件到缓存
    public static void loadConfig(String fileName) {

    	//是否已经载入
    	if (isLoaded(fileName)) {
    		return;
    	}
    	
		//载入配置到缓存
    	cacheConfig(fileName, getConfig(fileName), false);
		
    	//增加监听
    	addListener(fileName, new ConfListener() {
			@Override
			public void receive(String propertyContent) {
				cacheConfig(fileName, propertyContent, true);
			}
		});
    }
    
    //载入缓存
    public static void cacheConfig(String fileName, String config, boolean isListen) {
    	if (StringUtil.isNotEmpty(config)) {
			String[] contents = config.split(Constant.LINE_SEP);
			Map<String, String> cache = new HashMap<>();
			for (String content : contents) {
				String[] keyvalue = ConfHandlerSupport.getKeyValue(content);
				if (keyvalue != null) {
					cache.put(keyvalue[0], keyvalue[1]);
				}
			}
			cacheFileMap.put(fileName, cache);
	    	if (isListen) {
		    	SpringUtil.getBean(AutoUpdateConfigChangeListener.class).onChange(cache);//监听配置
	    	}
	    	if (logger.isDebugEnabled()) {
				logger.debug("{} is updated",fileName);
			}
		} else {
			cacheFileMap.remove(fileName);
			if (logger.isDebugEnabled()) {
				logger.debug("{} is removed",fileName);
			}
		}
    }
	
    //获取自定义文件
	public static String getConfig(String fileName) {
		//扩展点
		for (ConfigHandler handler : serviceLoader) {
			if (ConfHandlerSupport.isGlobal(fileName)) {
				return handler.getConfig(fileName,"global");
			} else {
				return handler.getConfig(fileName,ConfClient.getAppName());
			}
		}
    	return null;
	}
	
	//监听自定义文件变化
	public static void addListener(String fileName, ConfListener listener) {
		for (ConfigHandler handler : serviceLoader) {
			if (ConfHandlerSupport.isGlobal(fileName)) {
				handler.addListener(fileName, "global", listener);				
			} else {
				handler.addListener(fileName, ConfClient.getAppName(), listener);
			}
		}
	}
	
	//判断是否已经载入过配置
	public static boolean isLoaded(String fileName) {
		return cacheFileMap.containsKey(fileName);
	}
	
    //获取配置文件
	public static String getValue(String key, String fileName) {
		
		// 缓存获取
		Map<String, String> cache = cacheFileMap.get(fileName);
		if (cache != null && cache.containsKey(key)) {
			return cache.get(key);
		}

		return null;
	}
	
	public static void updateValue(String key, String value, String fileName) {
		Map<String, String> cache = cacheFileMap.get(fileName);
		if (cache == null) {
			synchronized(fileName.intern()) {
				cache = cacheFileMap.get(fileName);
				if (cache == null) {
					cache = new ConcurrentHashMap<>();
					cacheFileMap.put(fileName, cache);
				}
				
			}
		}
		cache.put(key, value);
		if (logger.isDebugEnabled()) {
			logger.debug("{} key:{} value:{} is updated",fileName,key,value);
		}
	}
}
