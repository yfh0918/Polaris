package com.polaris.comm.config;

import java.io.File;
import java.util.ServiceLoader;

import com.github.pagehelper.util.StringUtil;
import com.polaris.comm.util.PropertyUtils;

public  class ConfigHandlerProvider {

    private final ServiceLoader<ConfigHandler> serviceLoader = ServiceLoader.load(ConfigHandler.class);
    
    private static final ConfigHandlerProvider INSTANCE = new ConfigHandlerProvider();

    public static ConfigHandlerProvider getInstance() {
        return INSTANCE;
    }

	public String getKey(String key, boolean isWatch) {
		
		for (ConfigHandler handler : serviceLoader) {
			String result = handler.getKey(key, isWatch);
			if (StringUtil.isNotEmpty(result)) {
				return result;
			}
		}
		String[] files = ConfClient.getExtensionProperties();
		if (files != null) {
			for (String filename : files) {
				try {
					String propertyValue = PropertyUtils.readData("config" + File.separator + filename, key, false);
					if (propertyValue != null) {
						return propertyValue;
					}
				} catch (Exception ex) {
					//nothing
				}
			}
		}
		return null;
	}
}
