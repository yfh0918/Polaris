package com.polaris.config.zk;

import java.util.List;

import com.polaris.comm.config.ConfigHandler;

public class ConfZkHandler implements ConfigHandler{

	@Override
	public String getDataByKey(String nameSpace, String group, String key, boolean isWatch) {
		return ConfZkClient.getPathDataByKey(nameSpace + Constant.SLASH + group+Constant.SLASH + key, isWatch);
	}

	@Override
	public List<String> getAllKeys(String nameSpace, String group) {
		return ConfZkClient.getAllKeyByAppName(nameSpace + Constant.SLASH + group);
	}

	@Override
	public boolean deleteDataByKey(String nameSpace, String group, String key, boolean isWatch) {
		return ConfZkClient.deletePathByKey(nameSpace + Constant.SLASH + group+Constant.SLASH + key, isWatch);
	}

	@Override
	public boolean setDataByKey(String nameSpace, String group, String key, String data, boolean isWatch) {
		return ConfZkClient.setPathDataByKey(nameSpace + Constant.SLASH + group+Constant.SLASH + key, data, isWatch);
	}

}
