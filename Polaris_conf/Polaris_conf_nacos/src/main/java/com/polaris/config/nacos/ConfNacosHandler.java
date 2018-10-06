package com.polaris.config.nacos;

import com.polaris.comm.config.ConfListener;
import com.polaris.comm.config.ConfigHandler;

public class ConfNacosHandler implements ConfigHandler {

	@Override
	public String getValue(String key, boolean isWatch) {
		return ConfNacosClient.getInstance().getConfig(key);
	}
	
	@Override
	public void addListener(String fileName, ConfListener listener) {
		ConfNacosClient.getInstance().addListener(fileName, listener);
	}
	
	@Override
	public String getFileContent(String fileName) {
		return ConfNacosClient.getInstance().getFileContent(fileName);
	}
}
