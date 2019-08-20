package com.polaris.config.zk;

import com.polaris.core.config.ConfClient;
import com.polaris.core.config.ConfigHandler;
import com.polaris.core.util.StringUtil;

public class ConfZkHandler implements ConfigHandler {

	@Override
	public String getValue(String key, String fileName) {
		if (StringUtil.isNotEmpty(ConfClient.getNameSpace())) {
			return ConfZkClient.getPathDataByKey(ConfClient.getNameSpace() + Constant.SLASH + ConfClient.getAppName()+Constant.SLASH + key);
		} else {
			return ConfZkClient.getPathDataByKey(Constant.DEFAULT_VALUE + Constant.SLASH + ConfClient.getAppName()+Constant.SLASH + key);
		}
	}
}
