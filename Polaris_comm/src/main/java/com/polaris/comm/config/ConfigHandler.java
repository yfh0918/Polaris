package com.polaris.comm.config;

import java.util.List;

public interface ConfigHandler {

	String getDataByKey(String key, boolean isWarch);
	
	List<String> getAllKeys(String appName);
}
