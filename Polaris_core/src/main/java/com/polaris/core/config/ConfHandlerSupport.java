package com.polaris.core.config;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.core.Constant;
import com.polaris.core.util.EncryptUtil;
import com.polaris.core.util.EnvironmentUtil;
import com.polaris.core.util.NetUtils;
import com.polaris.core.util.PropertyUtils;
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
			String files = ConfHandlerEnum.DEFAULT.get(Constant.PROJECT_EXTENSION_PROPERTIES);
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
			String files = ConfHandlerEnum.DEFAULT.get(Constant.PROJECT_GLOBAL_PROPERTIES);
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
	
	public static String getDecryptValue(String propVal) {
		//解密操作
		try {
			EncryptUtil encrypt = EncryptUtil.getInstance();
			propVal = encrypt.decrypt(EncryptUtil.START_WITH, propVal);
		} catch (Exception ex) {
			//nothing
		}
		return propVal;
	}
	
    //初始化default
    protected static void initDefault() {
    	// 启动字符集
    	System.setProperty(Constant.FILE_ENCODING, Constant.UTF_CODE);

		// 设置文件
    	String projectConfigLocation = System.getProperty(Constant.SPRING_CONFIG_LOCACTION);
    	if (StringUtil.isNotEmpty(projectConfigLocation)) {
    		Constant.DEFAULT_CONFIG_NAME = projectConfigLocation;
    	} else if (StringUtil.isNotEmpty(System.getProperty(Constant.PROJECT_CONFIG_NAME))) {
    		Constant.DEFAULT_CONFIG_NAME = System.getProperty(Constant.PROJECT_CONFIG_NAME);
    	}
    	
    	logger.info("{} loading start",Constant.DEFAULT_CONFIG_NAME);
    	//获取DEFAULT_CONFIG_NAME
		ConfHandlerEnum.DEFAULT.put(PropertyUtils.getPropertiesFileContent(Constant.DEFAULT_CONFIG_NAME));
		if (StringUtil.isNotEmpty(System.getProperty(Constant.IP_ADDRESS))) {
			ConfHandlerEnum.DEFAULT.put(Constant.IP_ADDRESS, System.getProperty(Constant.IP_ADDRESS));
		} else {
			ConfHandlerEnum.DEFAULT.put(Constant.IP_ADDRESS, NetUtils.getLocalHost());
		}
		logger.info("{} loading end",Constant.DEFAULT_CONFIG_NAME);
		
		//查询所有systemenv
    	logger.info("systemEnv loading start");
		for (Map.Entry<String, String> entry : EnvironmentUtil.getSystemEnvironment().entrySet()) {
			if (StringUtil.isNotEmpty(entry.getValue())) {
				ConfHandlerEnum.DEFAULT.put(entry.getKey(), entry.getValue());
			}
		}
    	logger.info("systemEnv loading end");
		
		//查询所有systemProperties
    	logger.info("systemProperties loading start");
		for (Map.Entry<String, String> entry : EnvironmentUtil.getSystemProperties().entrySet()) {
			if (StringUtil.isNotEmpty(entry.getValue())) {
				ConfHandlerEnum.DEFAULT.put(entry.getKey(), entry.getValue());
			}
		}
		logger.info("systemProperties loading end");
    }

}
