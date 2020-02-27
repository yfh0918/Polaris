package com.polaris.core.config;

import java.util.Map;
import java.util.Properties;

import com.polaris.core.Constant;
import com.polaris.core.config.provider.ConfCompositeProvider;
import com.polaris.core.config.reader.CofReaderFactory;
import com.polaris.core.util.EnvironmentUtil;
import com.polaris.core.util.NetUtils;
import com.polaris.core.util.StringUtil;

public class ConfSystemHandler {
	
	private ConfSystemHandler() {}
	
	public static ConfSystemHandler INSTANCE = new ConfSystemHandler();
	
	private Properties properties = null;

	public void init(ConfCompositeProvider provider) {
		// 启动字符集
    	System.setProperty(Constant.FILE_ENCODING, Constant.UTF_CODE);

		// 设置application.properties文件名
    	for (Map.Entry<Object, Object> entry : getProperties().entrySet()) {
    		provider.put(ConfigFactory.DEFAULT, entry.getKey().toString(), entry.getValue().toString());
		}
    	//IP地址
		if (StringUtil.isNotEmpty(System.getProperty(Constant.IP_ADDRESS))) {
			provider.put(ConfigFactory.DEFAULT, Constant.IP_ADDRESS, System.getProperty(Constant.IP_ADDRESS));
		} else {
			if (StringUtil.isEmpty(provider.get(ConfigFactory.DEFAULT, Constant.IP_ADDRESS))) {
				provider.put(ConfigFactory.DEFAULT, Constant.IP_ADDRESS, NetUtils.getLocalHost());
			}
		}
		cleaProperties();
		
		//设置systemenv
		for (Map.Entry<String, String> entry : EnvironmentUtil.getSystemEnvironment().entrySet()) {
			if (StringUtil.isNotEmpty(entry.getValue())) {
				provider.put(ConfigFactory.DEFAULT, entry.getKey(), entry.getValue());
			}
		}
		
		//设置systemProperties
		for (Map.Entry<String, String> entry : EnvironmentUtil.getSystemProperties().entrySet()) {
			if (StringUtil.isNotEmpty(entry.getValue())) {
				provider.put(ConfigFactory.DEFAULT, entry.getKey(), entry.getValue());
			}
		}
		
	}
    
    private void cleaProperties() {
		this.properties.clear();
		this.properties = null;
	}
	
	public Properties getProperties() {
		if (this.properties != null) {
			return this.properties;
		}
		// 设置application.properties文件名
    	Properties propeties = null;
    	String file = null;
    	String projectConfigLocation = System.getProperty(Constant.SPRING_CONFIG_LOCACTION);
    	if (StringUtil.isNotEmpty(projectConfigLocation)) {
    		file = projectConfigLocation;
    	} else if (StringUtil.isNotEmpty(System.getProperty(Constant.PROJECT_CONFIG_NAME))) {
    		file = System.getProperty(Constant.PROJECT_CONFIG_NAME);
    	}
    	
    	//外部指定直接读取
    	if (StringUtil.isNotEmpty(file)) {
    		propeties = CofReaderFactory.get(file).getProperties(file,true);
    		Constant.DEFAULT_CONFIG_NAME = file;
        	return propeties;
    	} 
    	
    	//读取application.properties-不包括classpath
    	propeties = CofReaderFactory.get(Constant.DEFAULT_CONFIG_NAME).getProperties(Constant.DEFAULT_CONFIG_NAME, false);
    	file = Constant.DEFAULT_CONFIG_NAME;
		
		//读取application.yaml-包括classpath
    	if (propeties == null) {
    		propeties = CofReaderFactory.get(Constant.DEFAULT_CONFIG_NAME_YAML).getProperties(Constant.DEFAULT_CONFIG_NAME_YAML, true);
    		file = Constant.DEFAULT_CONFIG_NAME;
    	}
    	
    	//读取application.properties-包括classpath
    	if (propeties == null) {
    		propeties = CofReaderFactory.get(Constant.DEFAULT_CONFIG_NAME).getProperties(Constant.DEFAULT_CONFIG_NAME, true);
    		file = Constant.DEFAULT_CONFIG_NAME;
    	}
    	
    	//获取返回
    	Constant.DEFAULT_CONFIG_NAME = file;
    	this.properties = propeties;
    	return propeties;
	}
	

}
