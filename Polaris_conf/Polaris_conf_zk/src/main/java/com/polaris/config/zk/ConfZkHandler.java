package com.polaris.config.zk;

import com.polaris.comm.config.ConfigHandler;

public class ConfZkHandler implements ConfigHandler {

	@Override
	public String getKey(String env, String nameSpace, String cluster, String group, String key, boolean isWatch) {
		return ConfZkClient.getPathDataByKey(nameSpace + Constant.SLASH + group+Constant.SLASH + key, isWatch);
	}
}
