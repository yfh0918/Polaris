package com.polaris.config;

import java.util.List;

import com.polaris.comm.config.ConfigHandler;

public class ConfZkHandler implements ConfigHandler{

	@Override
	public String getDataByKey(String key, boolean isWarch) {
		return ConfZkClient.getPathDataByKey(key, isWarch);
	}

	@Override
	public List<String> getAllKeys(String appName) {
		return ConfZkClient.getAllKeyByAppName(appName);
	}

}
