package com.polaris.config.nacos;

import com.polaris.comm.config.ConfigHandler;

public class ConfNacosHandler implements ConfigHandler {

	@Override
	public String getKey(String key, boolean isWatch) {
		return ConfNacosClient.getInstance().getConfig(key);
	}
}
