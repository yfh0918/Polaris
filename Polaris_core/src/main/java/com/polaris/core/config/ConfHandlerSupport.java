package com.polaris.core.config;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.core.Constant;
import com.polaris.core.util.StringUtil;

public class ConfHandlerSupport {

	private static final Logger logger = LoggerFactory.getLogger(ConfHandlerSupport.class);
	private static Set<String> globalSet = new HashSet<>();


	/**
	* 获取扩展配置信息
	* @param 
	* @return 
	* @Exception 
	* @since 
	*/
	public static String[] getExtensionProperties() {
		try {
			//从本地获取
			String files = ConfigEnum.DEFAULT.get(Constant.PROJECT_EXTENSION_PROPERTIES);
			if (StringUtil.isEmpty(files)) {
				return null;
			}
			return files.split(",");
		} catch (Exception ex) {
			logger.error("getExtensionProperties is error");
		}
		return null;
	}
	
	/**
	* 获取全局配置信息
	* @param 
	* @return 
	* @Exception 
	* @since 
	*/
	public static String[] getGlobalProperties() {
		try {
			//从本地获取
			String files = ConfigEnum.DEFAULT.get(Constant.PROJECT_GLOBAL_PROPERTIES);
			if (StringUtil.isEmpty(files)) {
				return null;
			}
			String[] fileList = files.split(",");
			for (String file : fileList) {
				globalSet.add(file);
			}
			return fileList;
		} catch (Exception ex) {
			logger.error("getGlobalProperties is error");
		}
		return null;
	}
	public static boolean isGlobal(String fileName) {
		return globalSet.contains(fileName);
	}
	
	/**
	* 获取KV对
	* @param 
	* @return 
	* @Exception 
	* @since 
	*/
	public static String[] getKeyValue(String line) {
		if (StringUtil.isNotEmpty(line)) {
			String[] keyvalue = line.split("=");
			if (keyvalue.length == 0) {
				return new String[] {"",""};
			}
			if (keyvalue.length == 1) {
				return new String[] {keyvalue[0].trim(),""};
			}
			String value = "";
			for (int index = 0; index < keyvalue.length; index++) {
				if (index != 0) {
					if (StringUtil.isEmpty(value)) {
						value = keyvalue[index].trim();
					} else {
						value = value + "=" + keyvalue[index].trim();
					}
				}
			}
			return new String[] {keyvalue[0].trim(),value};
		}
		return null;
	}

}
