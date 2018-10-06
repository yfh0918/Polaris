package com.polaris.comm.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ServiceLoader;

import com.github.pagehelper.util.StringUtil;
import com.polaris.comm.Constant;
import com.polaris.comm.util.PropertyUtils;

public  class ConfigHandlerProvider {

    private final ServiceLoader<ConfigHandler> serviceLoader = ServiceLoader.load(ConfigHandler.class);
    
    private static final ConfigHandlerProvider INSTANCE = new ConfigHandlerProvider();

    public static ConfigHandlerProvider getInstance() {
        return INSTANCE;
    }

	public String getValue(String key, boolean isWatch) {
		
		//配置最优先
		try {
			String propertyValue = PropertyUtils.readData(Constant.PROJECT_PROPERTY, key, false);
			if (propertyValue != null) {
				return propertyValue;
			}
		} catch (Exception ex) {
			//nothing
		}

		// 扩展点
		for (ConfigHandler handler : serviceLoader) {
			String result = handler.getValue(key, isWatch);
			if (StringUtil.isNotEmpty(result)) {
				return result;
			}
		}
		
		// 扩展文件
		String[] files = getExtensionProperties();
		if (files != null) {
			for (String filename : files) {
				try {
					String propertyValue = PropertyUtils.readData(Constant.CONFIG + File.separator + filename, key, false);
					if (propertyValue != null) {
						return propertyValue;
					}
				} catch (Exception ex) {
					//nothing
				}
			}
		}
		
		// 兜底
		try {
			String propertyValue = PropertyUtils.readData(Constant.CONFIG,"application", "properties", key, false);
			if (propertyValue != null) {
				return propertyValue;
			}
		} catch (Exception ex) {
			//nothing
		}
		

		return null;
	}
	
	public void addListener(String fileName, ConfListener listener) {
		//扩展点
		for (ConfigHandler handler : serviceLoader) {
			handler.addListener(fileName, listener);
		}
	}
	
	public String getFileContent(String fileName) {
		
		//扩展点
		for (ConfigHandler handler : serviceLoader) {
			String content = handler.getFileContent(fileName);
			if (StringUtil.isNotEmpty(content)) {
				return content;
			}
		}
		
		//本地文件
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
	
	/**
	* 获取配置信息
	* @param 
	* @return 
	* @Exception 
	* @since 
	*/
	public static String[] getExtensionProperties() {
		try {
			String files = PropertyUtils.readData(Constant.PROJECT_PROPERTY, Constant.PROJECT_EXTENSION_PROPERTIES, false);
			if (StringUtil.isNotEmpty(files)) {
				return files.split(",");
			}
		} catch (Exception ex) {
			//nothing
		}
		return null;
	}

}
