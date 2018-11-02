package com.polaris.comm.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

import com.github.pagehelper.util.StringUtil;
import com.polaris.comm.Constant;
import com.polaris.comm.util.LogUtil;
import com.polaris.comm.util.PropertyUtils;

public  class ConfigHandlerProvider {

	private static final LogUtil logger = LogUtil.getInstance(ConfigHandlerProvider.class);
	
    private static final ServiceLoader<ConfigHandler> serviceLoader = ServiceLoader.load(ConfigHandler.class);
    
    private static final ConfigHandlerProvider INSTANCE = new ConfigHandlerProvider();
    
	private static volatile Map<String, Map<String, String>> cacheFileMap = new ConcurrentHashMap<>();

    // 监听所有扩展的文件
    private ConfigHandlerProvider() {
    }

    public static ConfigHandlerProvider getInstance() {
        return INSTANCE;
    }
	
    //获取文件
	public String getConfig(String fileName) {
		//扩展点
		for (ConfigHandler handler : serviceLoader) {
			String config = handler.getConfig(fileName);
			if (StringUtil.isNotEmpty(config)) {
				return config;
			}
		}
		return getLocalFileContent(fileName);
	}
	
	//监听
	public void addListener(String fileName, ConfListener listener) {
		//扩展点
		for (ConfigHandler handler : serviceLoader) {
			handler.addListener(fileName, listener);
		}
	}
	
    // 获取key,value
	public String getValue(String key, String fileName, boolean isWatch) {
		
		// 缓存获取
		Map<String, String> cache = cacheFileMap.get(fileName);
		if (cache != null && cache.containsKey(key)) {
			return cache.get(key);
		}

		// 扩展点(application.properties除外)
		if (!Constant.DEFAULT_CONFIG_NAME.equals(fileName)) {
			for (ConfigHandler handler : serviceLoader) {
				String result = handler.getValue(key, fileName, isWatch);
				if (StringUtil.isNotEmpty(result)) {
					updateCache(key, result, fileName);
					return result;
				}
			}
		}
		
		// 本地配置（默认文件）
		try {
			String propertyValue = PropertyUtils.readData(Constant.CONFIG + File.separator + fileName, key, false);
			if (propertyValue != null) {
				updateCache(key, propertyValue, fileName);
				return propertyValue;
			}
		} catch (Exception ex) {
			//nothing
		}
		
		return null;
	}
	
	/**
	* 获取配置信息
	* @param 
	* @return 
	* @Exception 
	* @since 
	*/
	public static String[] getExtensionProperties() {
		try {
			//从本地获取
			String files = PropertyUtils.readData(Constant.PROJECT_PROPERTY, Constant.PROJECT_EXTENSION_PROPERTIES, false);
			return files.split(",");
		} catch (Exception ex) {
			//nothing
		}
		return null;
	}
	
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
	
	public static String[] getKeyValue(String line) {
		if (StringUtil.isNotEmpty(line)) {
			String[] keyvalue = line.split("=");
			if (keyvalue.length == 0) {
				return new String[] {"",""};
			}
			if (keyvalue.length == 1) {
				return new String[] {keyvalue[0].trim(),""};
			}
			return new String[] {keyvalue[0].trim(),keyvalue[1].trim()};
		}
		return null;
	}
		
	//获取整个文件的内容
	@SuppressWarnings("rawtypes")
	public static String getLocalFileContent(String fileName) {
		
		// propertyies
		if (fileName.toLowerCase().endsWith(".properties")) {
			StringBuffer buffer = new StringBuffer();
			try (InputStream in = ConfigHandlerProvider.class.getClassLoader().getResourceAsStream(Constant.CONFIG + File.separator + fileName)) {
	            Properties p = new Properties();
	            p.load(in);
	            for (Map.Entry entry : p.entrySet()) {
	                String key = (String) entry.getKey();
	                buffer.append(key + "=" + entry.getValue());
	                buffer.append(Constant.LINE_SEP);
	            }
	        } catch (IOException e) {
	           // nothing;
	        }
			return buffer.toString();
		}
		
		// 非propertyies
		try (InputStream inputStream = ConfigHandlerProvider.class.getClassLoader().getResourceAsStream(Constant.CONFIG + File.separator + fileName)) {
			InputStreamReader reader = new InputStreamReader(inputStream, Charset.defaultCharset());
			BufferedReader bf= new BufferedReader(reader);
			StringBuffer buffer = new StringBuffer();
			String line = bf.readLine();
	        while (line != null) {
	        	buffer.append(line);
	            line = bf.readLine();
	        	buffer.append(Constant.LINE_SEP);
	        }
	        String content = buffer.toString();
	        if (StringUtil.isNotEmpty(content)) {
	        	return content;
	        }
        } catch (IOException e) {
        	//nothing
        }
		return null;
	}
	
}
