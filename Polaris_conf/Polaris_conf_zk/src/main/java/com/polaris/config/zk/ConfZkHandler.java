package com.polaris.config.zk;

import com.polaris.core.config.ConfListener;
import com.polaris.core.config.ConfigHandler;

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
