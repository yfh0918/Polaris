package com.polaris.core.config;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

import com.github.pagehelper.util.StringUtil;
import com.polaris.core.Constant;
import com.polaris.core.util.LogUtil;

public  class ConfigHandlerProvider {

	private static final LogUtil logger = LogUtil.getInstance(ConfigHandlerProvider.class);
	
    private static final ServiceLoader<ConfigHandler> serviceLoader = ServiceLoader.load(ConfigHandler.class);
    
	private static volatile Map<String, Map<String, String>> cacheFileMap = new ConcurrentHashMap<>();

	private static volatile Map<String, Map<String, String>> gobalCacheFileMap = new ConcurrentHashMap<>();

	// 监听所有扩展的文件
    private ConfigHandlerProvider() {
    	
    }
    
    // 载入文件到缓存
    public static void loadConfig(String fileName, boolean isGlobal) {
    	
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
				}
			}
			});

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
		}

    }
	
    //获取自定义文件
	public static String getConfig(String fileName, boolean isGlobal) {
		//扩展点
    	if (!Constant.DEFAULT_CONFIG_NAME.equals(fileName)) {
    		for (ConfigHandler handler : serviceLoader) {
    			String config = handler.getConfig(fileName,ConfHandlerSupport.getGroup(isGlobal));
    			if (StringUtil.isNotEmpty(config)) {
    				return config;
    			}
    		}
    	}
		return ConfHandlerSupport.getLocalFileContent(fileName);
	}
	
	//监听自定义文件变化
	public static void addListener(String fileName, boolean isGlobal, ConfListener listener) {
		if (!Constant.DEFAULT_CONFIG_NAME.equals(fileName)) {
			for (ConfigHandler handler : serviceLoader) {
				handler.addListener(fileName, ConfHandlerSupport.getGroup(isGlobal), listener);
			}
		}
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
	public static void removeCache(String fileName) {
		Map<String, String> cache = cacheFileMap.get(fileName);
		if (cache != null) {
			logger.info(">>>>>>>>>> conf: 删除配置：file:{} ", fileName);
			cacheFileMap.remove(fileName);
		}
	}
	public static void removeCache(String key, String fileName) {
		Map<String, String> cache = cacheFileMap.get(fileName);
		if (cache != null) {
			logger.info(">>>>>>>>>> conf: 删除配置：file:{}, key:{} ", fileName, key);
			cache.remove(key);
		}
	}
	
	public static void updateCache(String fileName, Map<String, String> cache) {
		updateCache(fileName, cache, false);
	}
	public static void updateCache(String fileName, Map<String, String> cache, boolean isGlobal) {
		logger.info(">>>>>>>>>> conf: 跟新配置：file:{}", fileName);
		cacheFileMap.put(fileName, cache);
	}
	public static void updateCache(String key, String value, String fileName) {
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
	
	
}
