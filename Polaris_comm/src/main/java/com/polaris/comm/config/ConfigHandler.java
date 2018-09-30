package com.polaris.comm.config;

public interface ConfigHandler {
	String getKey(String env, String nameSpace, String cluster, String appName, String key, boolean isWatch);
}
