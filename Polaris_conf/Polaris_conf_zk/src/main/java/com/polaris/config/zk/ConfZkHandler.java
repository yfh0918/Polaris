package com.polaris.config.zk;

import com.polaris.comm.config.ConfClient;
import com.polaris.comm.config.ConfigHandler;

public class ConfZkHandler implements ConfigHandler {

	@Override
	public String getValue(String key, boolean isWatch) {
		return ConfZkClient.getPathDataByKey(ConfClient.getNameSpace() + Constant.SLASH + ConfClient.getAppName()+Constant.SLASH + key, isWatch);
	}
}
