package com.polaris.core.config;

import java.util.Properties;

import com.polaris.core.Constant;
import com.polaris.core.util.NetUtils;
import com.polaris.core.util.PropertyUtils;
import com.polaris.core.util.StringUtil;
import com.polaris.core.util.YamlUtil;

public abstract class ConfPropertyAdapt {
	private static final String YAML_SUFFIX = ".yaml";
	private static Properties rootProperties = null;
	
	public static Properties getRootProperties() {
		if (rootProperties != null) {
			return rootProperties;
		}
		// 设置application.properties文件名
    	Properties propeties = null;
    	String rootConfigName = null;
    	String projectConfigLocation = System.getProperty(Constant.SPRING_CONFIG_LOCACTION);
    	if (StringUtil.isNotEmpty(projectConfigLocation)) {
    		rootConfigName = projectConfigLocation;
    	} else if (StringUtil.isNotEmpty(System.getProperty(Constant.PROJECT_CONFIG_NAME))) {
    		rootConfigName = System.getProperty(Constant.PROJECT_CONFIG_NAME);
    	}
    	
    	//外部指定直接读取
    	if (StringUtil.isNotEmpty(rootConfigName)) {
    		propeties = getProperties(rootConfigName,true);
    		Constant.DEFAULT_CONFIG_NAME = rootConfigName;
        	return propeties;
    	} 
    	
    	//读取application.properties-不包括classpath
    	propeties = getProperties(Constant.DEFAULT_CONFIG_NAME, false);
		rootConfigName = Constant.DEFAULT_CONFIG_NAME;
		
		//读取application.yaml-包括classpath
    	if (propeties == null) {
    		propeties = getProperties(Constant.DEFAULT_CONFIG_NAME_YAML, true);
    		rootConfigName = Constant.DEFAULT_CONFIG_NAME;
    	}
    	
    	//读取application.properties-包括classpath
    	if (propeties == null) {
    		propeties = getProperties(Constant.DEFAULT_CONFIG_NAME, true);
    		rootConfigName = Constant.DEFAULT_CONFIG_NAME;
    	}
    	
    	//IP地址
		if (StringUtil.isNotEmpty(System.getProperty(Constant.IP_ADDRESS))) {
			propeties.put(Constant.IP_ADDRESS, System.getProperty(Constant.IP_ADDRESS));
		} else {
			if (StringUtil.isEmpty(propeties.getProperty(Constant.IP_ADDRESS))) {
				propeties.put(Constant.IP_ADDRESS, NetUtils.getLocalHost());
			}
		}
		
    	//获取返回
    	Constant.DEFAULT_CONFIG_NAME = rootConfigName;
    	rootProperties = propeties;
    	return propeties;
	}
	
	public static void cleaRootProperties() {
		rootProperties.clear();
		rootProperties = null;
	}
	
	public static Properties getProperties(String fileName) {
		return getProperties(fileName, true);
	}
	public static Properties getProperties(String fileName, boolean includeClassPath) {
		if (fileName.toLowerCase().endsWith(YAML_SUFFIX)) {
			return YamlUtil.getProperties(fileName, includeClassPath);
		}
		return PropertyUtils.getProperties(fileName, includeClassPath);
	}
	
	public static Properties getProperties(String fileName, String lines) {
		if (fileName.toLowerCase().endsWith(YAML_SUFFIX)) {
	        return YamlUtil.getProperties(lines); 
		}
		return PropertyUtils.getProperties(fileName, lines);
    }
}
