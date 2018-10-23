package com.polaris.config.nacos;

import com.polaris.comm.config.ConfListener;
import com.polaris.comm.config.ConfigHandler;

public class ConfNacosHandler implements ConfigHandler {

	@Override
	public String getValue(String key, String fileName, boolean isWatch) {
		return ConfNacosClient.getInstance().getConfig(key, fileName);
	}
	
	@Override
	public String getConfig(String fileName) {
		return ConfNacosClient.getInstance().getConfig(fileName);
	}

	@Override
	public void addListener(String fileName, ConfListener listener) {
		ConfNacosClient.getInstance().addListener(fileName, listener);
	}

}
