package com.polaris.comm.config;

import java.util.List;

public interface ConfigHandler {

	List<String> getAllNameSpaces(boolean isWatch);
	boolean addNameSpace(String nameSpace, boolean isWatch);
	boolean deleteNameSpace(String nameSpace, boolean isWatch);

	List<String> getAllGroups(String namespace, boolean isWatch);
	boolean addGroup(String nameSpace, String group,  boolean isWatch);
	boolean deleteGroup(String nameSpace, String group,  boolean isWatch);

	List<String> getAllKeys(String nameSpace, String group,  boolean isWatch);

	String getKey(String nameSpace, String group, String key, boolean isWatch);
	
	boolean deleteKey(String nameSpace, String group, String key, boolean isWatch);

	boolean addKey(String nameSpace, String group, String key, String data, boolean isWatch);
}
