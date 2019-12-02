package com.polaris.config.zk;

import org.springframework.core.annotation.Order;

import com.polaris.core.config.ConfListener;
import com.polaris.core.config.ConfigHandler;

@Order(2)
public class ConfZkHandler implements ConfigHandler {

	@Override
	public String getConfig(String fileName, String group) {
		return ConfZkClient.getConfig(fileName,group);
	}

	@Override
	public void addListener(String fileName, String group, ConfListener listener) {
		ConfZkClient.addListener(fileName, group, listener);
	}
}
