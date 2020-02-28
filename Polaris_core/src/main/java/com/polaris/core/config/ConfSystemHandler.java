package com.polaris.core.config;

import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.core.Constant;
import com.polaris.core.config.provider.ConfCompositeProvider;
import com.polaris.core.config.reader.CofReaderFactory;
import com.polaris.core.util.EnvironmentUtil;
import com.polaris.core.util.FileUitl;
import com.polaris.core.util.NetUtils;
import com.polaris.core.util.StringUtil;

public class ConfSystemHandler {
	private static final Logger logger = LoggerFactory.getLogger(ConfSystemHandler.class);

	private static volatile String CONFIG_NAME = "application";

	private ConfSystemHandler() {}
	
	public static ConfSystemHandler INSTANCE = new ConfSystemHandler();
	
	private Properties properties = null;

	public void init(ConfCompositeProvider provider) {
		logger.info("application load start...");
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
    		propeties = CofReaderFactory.get(file).getProperties(file,true,true);
    	} 
    	
		//先搜索-path
    	if (propeties == null) {
    		for (String suffix : CofReaderFactory.SUPPORT_TYPE) {
        		file =  CONFIG_NAME + FileUitl.DOT + suffix;
        		propeties = CofReaderFactory.get(file).getProperties(file,true,false);
        		if (propeties != null) {
        			break;
        		}
        	}
        	
			//在搜索-classpath
    		if (propeties == null) {
    			for (String suffix : CofReaderFactory.SUPPORT_TYPE) {
            		file =  CONFIG_NAME + FileUitl.DOT + suffix;
            		propeties = CofReaderFactory.get(file).getProperties(file,false,true);
            		if (propeties != null) {
            			break;
            		}
            	}
    		}
    	}
    	
     	this.properties = propeties;
    	return propeties;
	}
	

}
