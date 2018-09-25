package com.polaris.config.nacos;

import java.util.List;

import com.polaris.comm.config.ConfigHandler;

public class ConfNacosHandler implements ConfigHandler {

	@Override
	public List<String> getAllNameSpaces(boolean isWatch) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean addNameSpace(String nameSpace, boolean isWatch) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteNameSpace(String nameSpace, boolean isWatch) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<String> getAllGroups(String namespace, boolean isWatch) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean addGroup(String nameSpace, String group, boolean isWatch) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteGroup(String nameSpace, String group, boolean isWatch) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<String> getAllKeys(String nameSpace, String group, boolean isWatch) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getKey(String nameSpace, String group, String key, boolean isWatch) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean deleteKey(String nameSpace, String group, String key, boolean isWatch) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addKey(String nameSpace, String group, String key, String data, boolean isWatch) {
		// TODO Auto-generated method stub
		return false;
	}



}
