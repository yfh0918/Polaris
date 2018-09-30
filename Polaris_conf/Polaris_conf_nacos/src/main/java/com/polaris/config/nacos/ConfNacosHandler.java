package com.polaris.config.nacos;

import com.polaris.comm.config.ConfigHandler;

public class ConfNacosHandler implements ConfigHandler {

	@Override
	public String getKey(String env, String nameSpace, String cluster, String appName, String key, boolean isWatch) {
		return ConfNacosClient.getInstance(nameSpace).getConfig(key);
	}
}
