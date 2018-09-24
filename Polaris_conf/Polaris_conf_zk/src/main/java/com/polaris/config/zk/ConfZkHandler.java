package com.polaris.config.zk;

import java.util.List;

import com.polaris.comm.config.ConfigHandler;

public class ConfZkHandler implements ConfigHandler {

	@Override
	public List<String> getAllNameSpaces(boolean isWatch) {
		return ConfZkClient.getChildren("", isWatch);
	}
	@Override
	public boolean addNameSpace(String nameSpace, boolean isWatch) {
		return ConfZkClient.setPathDataByKey(nameSpace, null, isWatch);
	}

	@Override
	public boolean deleteNameSpace(String nameSpace, boolean isWatch) {
		return ConfZkClient.deletePathByKey(nameSpace, isWatch);
	}
	
	@Override
	public List<String> getAllGroups(String namespace, boolean isWatch) {
		return ConfZkClient.getChildren(namespace, isWatch);
	}
	@Override
	public boolean addGroup(String nameSpace, String group, boolean isWatch) {
		return ConfZkClient.setPathDataByKey(nameSpace + Constant.SLASH + group, null, isWatch);
	}

	@Override
	public boolean deleteGroup(String nameSpace, String group, boolean isWatch) {
		return ConfZkClient.deletePathByKey(nameSpace + Constant.SLASH + group, isWatch);
	}

	@Override
	public List<String> getAllKeys(String nameSpace, String group, boolean isWatch) {
		return ConfZkClient.getAllKeyByAppName(nameSpace + Constant.SLASH + group, isWatch);
	}

	@Override
	public String getKey(String nameSpace, String group, String key, boolean isWatch) {
		return ConfZkClient.getPathDataByKey(nameSpace + Constant.SLASH + group+Constant.SLASH + key, isWatch);
	}

	@Override
	public boolean deleteKey(String nameSpace, String group, String key, boolean isWatch) {
		return ConfZkClient.deletePathByKey(nameSpace + Constant.SLASH + group+Constant.SLASH + key, isWatch);
	}

	@Override
	public boolean addKey(String nameSpace, String group, String key, String data, boolean isWatch) {
		return ConfZkClient.setPathDataByKey(nameSpace + Constant.SLASH + group+Constant.SLASH + key, data, isWatch);
	}

}
