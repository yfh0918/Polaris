package com.polaris.config.nacos;

import com.polaris.core.config.ConfListener;
import com.polaris.core.config.ConfigHandler;
import com.polaris.core.config.ConfigHandlerProvider;

public class ConfNacosHandler implements ConfigHandler {

	@Override
	public String getValue(String key, String fileName, boolean isWatch) {
		return ConfNacosClient.getInstance().getConfig(key, fileName, ConfigHandlerProvider.getConfigGroup());
	}
	
	@Override
	public String getConfig(String fileName) {
		return ConfNacosClient.getInstance().getConfig(fileName,ConfigHandlerProvider.getConfigGroup());
	}

	@Override
	public void addListener(String fileName, ConfListener listener) {
		ConfNacosClient.getInstance().addListener(fileName, ConfigHandlerProvider.getConfigGroup(), listener);
	}
}
