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
import com.polaris.core.util.PropertyUtils;
import com.polaris.core.util.SpringUtil;

public abstract class ConfigHandlerProvider {

	private static final Logger logger = LoggerFactory.getLogger(ConfigHandlerProvider.class);
	
    private static final ServiceLoader<ConfigHandler> serviceLoader = ServiceLoader.load(ConfigHandler.class);
    
	private static volatile Map<String, Map<String, String>> cacheFileMap = new ConcurrentHashMap<>();

	private static volatile Map<String, Map<String, String>> gobalCacheFileMap = new ConcurrentHashMap<>();
    
    // 载入文件到缓存
    public static void loadConfig(String fileName, boolean isGlobal) {

    	//是否已经载入
    	if (isLoaded(fileName, isGlobal)) {
    		return;
    	}
    	
		//载入配置到缓存
    	String config = getConfig(fileName, isGlobal);
		if (StringUtil.isNotEmpty(config)) {
			String[] contents = config.split(Constant.LINE_SEP);
			Map<String, String> cache = new HashMap<>();
			for (String content : contents) {
				String[] keyvalue = ConfHandlerSupport.getKeyValue(content);
				if (keyvalue != null) {
					cache.put(keyvalue[0], keyvalue[1]);
					logger.info(">>>>>>>>>> conf: 更新配置：file:{}, key:{} , value:{}", fileName, keyvalue[0], keyvalue[1]);
				}
			}
	    	if (isGlobal) {
	    		gobalCacheFileMap.put(fileName, cache);
			} else {
				cacheFileMap.put(fileName, cache);
			}
		} else {
			if (isGlobal) {
	    		gobalCacheFileMap.remove(fileName);
			} else {
				cacheFileMap.remove(fileName);
			}
		}
		
    	//增加监听
    	addListener(fileName, isGlobal, new ConfListener() {
			@Override
			public void receive(String propertyContent) {
				if (StringUtil.isNotEmpty(propertyContent)) {
					String[] contents = propertyContent.split(Constant.LINE_SEP);
					Map<String, String> cache = new HashMap<>();
					for (String content : contents) {
						String[] keyvalue = ConfHandlerSupport.getKeyValue(content);
						if (keyvalue != null) {
							cache.put(keyvalue[0], keyvalue[1]);
							logger.info(">>>>>>>>>> conf: 更新配置：file:{}, key:{} , value:{}", fileName, keyvalue[0], keyvalue[1]);
						}
					}
					if (isGlobal) {
			    		gobalCacheFileMap.put(fileName, cache);
					} else {
						cacheFileMap.put(fileName, cache);
					}
					SpringUtil.getBean(AutoUpdateConfigChangeListener.class).onChange(cache);//监听配置
					
				} else {
					if (isGlobal) {
			    		gobalCacheFileMap.remove(fileName);
					} else {
						cacheFileMap.remove(fileName);
					}
				}
			}
			});



    }
	
    //获取自定义文件
	public static String getConfig(String fileName, boolean isGlobal) {
		//扩展点
    	if (!Constant.DEFAULT_CONFIG_NAME.equals(fileName)) {
    		for (ConfigHandler handler : serviceLoader) {
    			String config = handler.getConfig(fileName,getGroup(isGlobal));
    			if (StringUtil.isNotEmpty(config)) {
    				return config;
    			}
    		}
    	} else {
    		return PropertyUtils.getPropertiesFileContent(Constant.DEFAULT_CONFIG_NAME);
    	}
    	return null;
	}
	
	//监听自定义文件变化
	public static void addListener(String fileName, boolean isGlobal, ConfListener listener) {
		if (!Constant.DEFAULT_CONFIG_NAME.equals(fileName)) {
			for (ConfigHandler handler : serviceLoader) {
				handler.addListener(fileName, getGroup(isGlobal), listener);
			}
		}
		
	}
	
	//判断是否已经载入过配置
	public static boolean isLoaded(String fileName, boolean isGlobal) {
		if (!isGlobal) {
			return cacheFileMap.containsKey(fileName);
		}
		return gobalCacheFileMap.containsKey(fileName);
	}
	
    //获取配置文件
	public static String getValue(String key, String fileName, boolean isGlobal) {
		
		// 缓存获取
		if (isGlobal) {
			Map<String, String> cache = gobalCacheFileMap.get(fileName);
			if (cache != null && cache.containsKey(key)) {
				return cache.get(key);
			}
			
		} else {
			Map<String, String> cache = cacheFileMap.get(fileName);
			if (cache != null && cache.containsKey(key)) {
				return cache.get(key);
			}
		}

		return null;
	}
	
	//一下对缓存的处理都只针对 非全局缓存
	public static void removeValue(String key, String fileName) {
		Map<String, String> cache = cacheFileMap.get(fileName);
		if (cache != null) {
			logger.info(">>>>>>>>>> conf: 删除配置：file:{}, key:{} ", fileName, key);
			cache.remove(key);
		}
	}
	public static void updateValue(String key, String value, String fileName) {
		Map<String, String> cache = cacheFileMap.get(fileName);
		boolean isAdd = false;
		if (cache == null) {
			synchronized(fileName.intern()) {
				cache = cacheFileMap.get(fileName);
				if (cache == null) {
					cache = new ConcurrentHashMap<>();
					cacheFileMap.put(fileName, cache);
					isAdd = true;
				}
				
			}
		}
		if (cache.get(key) == null) {
			isAdd = true;
		}
		if (isAdd) {
			logger.info(">>>>>>>>>> conf: 新增配置：file:{}, key:{} , value:{}", fileName, key, value);
		} else {
			logger.info(">>>>>>>>>> conf: 更新配置：file:{}, key:{} , value:{}", fileName, key, value);
		}
		cache.put(key, value);
	}
	
	//获取分组名称
	public static String getGroup(boolean isGlobal) {
		if (isGlobal) {
			return getValue(Constant.PROJECR_GLOBAL_CONFIG_NAME, Constant.DEFAULT_CONFIG_NAME, false);
		}
		return ConfClient.getAppName();
	}
}
