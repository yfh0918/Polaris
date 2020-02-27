package com.polaris.core.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import com.polaris.core.Constant;
import com.polaris.core.util.FileUitl;
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
        
		try (InputStream in = FileUitl.getStream(fileName,includeClassPath)) {
			if (in == null) {
				return null;
		    }
        	//yaml文件适配
      		if (fileName.toLowerCase().endsWith(YAML_SUFFIX)) {
      			return YamlUtil.yaml2Properties(in);
      		} 
      		//property文件
      		return PropertyUtils.getProperties(in);
            
	    } catch (IOException e) {
		   e.printStackTrace();
	    }
	    return null;
	}
	
	public static Map<String, Object> getMap(String fileName, String lines) {
		
		//yaml文件适配
		if (fileName.toLowerCase().endsWith(YAML_SUFFIX)) {
	        return YamlUtil.yaml2Map(lines); 
		}
		
		//properties
		return PropertyUtils.getMap(fileName, lines);
    }
}
