package com.polaris.comm.config;

import java.util.List;

public interface ConfigHandler {

	String getDataByKey(String nameSpace, String group, String key, boolean isWatch);
	
	List<String> getAllKeys(String nameSpace, String group);
	
	boolean deleteDataByKey(String nameSpace, String group, String key, boolean isWatch);

	boolean setDataByKey(String nameSpace, String group, String key, String data, boolean isWatch);
}
