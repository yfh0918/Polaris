package com.polaris.config.zk;

import com.polaris.comm.config.ConfClient;
import com.polaris.comm.config.ConfigHandler;
import com.polaris.comm.util.StringUtil;

public class ConfZkHandler implements ConfigHandler {

	@Override
	public String getValue(String key, String fileName, boolean isWatch) {
		if (StringUtil.isNotEmpty(ConfClient.getNameSpace())) {
			return ConfZkClient.getPathDataByKey(ConfClient.getNameSpace() + Constant.SLASH + ConfClient.getAppName()+Constant.SLASH + key, isWatch);
		} else {
			return ConfZkClient.getPathDataByKey(Constant.DEFAULT_VALUE + Constant.SLASH + ConfClient.getAppName()+Constant.SLASH + key, isWatch);
		}
	}
}
