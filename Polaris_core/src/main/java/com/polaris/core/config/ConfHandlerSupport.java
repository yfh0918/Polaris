package com.polaris.core.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Properties;

import com.github.pagehelper.util.StringUtil;
import com.polaris.core.Constant;
import com.polaris.core.util.PropertyUtils;

public class ConfHandlerSupport {




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
			String files = PropertyUtils.readData(Constant.PROJECT_PROPERTY, Constant.PROJECT_EXTENSION_PROPERTIES, false);
			return files.split(",");
		} catch (Exception ex) {
			//nothing
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
			String files = PropertyUtils.readData(Constant.PROJECT_PROPERTY, Constant.PROJECT_GLOBAL_PROPERTIES, false);
			return files.split(",");
		} catch (Exception ex) {
			//nothing
		}
		return null;
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
	
	/**
	* 获取整个文件的内容
	* @param 
	* @return 
	* @Exception 
	* @since 
	*/
	@SuppressWarnings("rawtypes")
	public static String getLocalFileContent(String fileName) {
		
		// propertyies
		if (fileName.toLowerCase().endsWith(".properties")) {
			StringBuffer buffer = new StringBuffer();
			try (InputStream in = ConfigHandlerProvider.class.getClassLoader().getResourceAsStream(Constant.CONFIG + File.separator + fileName)) {
	            Properties p = new Properties();
	            p.load(in);
	            for (Map.Entry entry : p.entrySet()) {
	                String key = (String) entry.getKey();
	                buffer.append(key + "=" + entry.getValue());
	                buffer.append(Constant.LINE_SEP);
	            }
	        } catch (IOException e) {
	           // nothing;
	        }
			return buffer.toString();
		}
		
		// 非propertyies
		try (InputStream inputStream = ConfigHandlerProvider.class.getClassLoader().getResourceAsStream(Constant.CONFIG + File.separator + fileName)) {
			InputStreamReader reader = new InputStreamReader(inputStream, Charset.defaultCharset());
			BufferedReader bf= new BufferedReader(reader);
			StringBuffer buffer = new StringBuffer();
			String line = bf.readLine();
	        while (line != null) {
	        	buffer.append(line);
	            line = bf.readLine();
	        	buffer.append(Constant.LINE_SEP);
	        }
	        String content = buffer.toString();
	        if (StringUtil.isNotEmpty(content)) {
	        	return content;
	        }
        } catch (IOException e) {
        	//nothing
        }
		return null;
	}
	
	//获取分组名称
	public static String getGroup(boolean isGlobal) {
		if (isGlobal) {
			return getGlobalConfigGroup();
		}
		return getConfigGroup();
	}
	// 获取配置中心的分组
	public static String getConfigGroup() {
		StringBuilder group = new StringBuilder();
		if (StringUtil.isNotEmpty(ConfClient.getEnv())) {
			group.append(ConfClient.getEnv());
			group.append(":");
		}
		if (StringUtil.isNotEmpty(ConfClient.getCluster())) {
			group.append(ConfClient.getCluster());
			group.append(":");
		}
		group.append(ConfClient.getAppName());
		return group.toString();
	}
	
	// 获取全局配置中心的分组
	public static String getGlobalConfigGroup() {
		StringBuilder group = new StringBuilder();
		if (StringUtil.isNotEmpty(ConfClient.getEnv())) {
			group.append(ConfClient.getEnv());
			group.append(":");
		}
		if (StringUtil.isNotEmpty(ConfClient.getCluster())) {
			group.append(ConfClient.getCluster());
			group.append(":");
		}
		group.append(ConfClient.getGlobalGroup());
		return group.toString();
	}
}
