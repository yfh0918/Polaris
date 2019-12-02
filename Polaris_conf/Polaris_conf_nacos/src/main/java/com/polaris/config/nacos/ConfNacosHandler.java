package com.polaris.config.nacos;

import org.springframework.core.annotation.Order;

import com.polaris.core.config.ConfListener;
import com.polaris.core.config.ConfigHandler;

@Order(0)
public class ConfNacosHandler implements ConfigHandler {

	@Override
	public String getConfig(String fileName, String group) {
		return ConfNacosClient.getInstance().getConfig(fileName,group);
	}

	@Override
	public void addListener(String fileName, String group, ConfListener listener) {
		ConfNacosClient.getInstance().addListener(fileName, group, listener);
	}
}
