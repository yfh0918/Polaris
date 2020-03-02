package com.polaris.core.config.provider;

import java.util.Map;
import java.util.Properties;

import com.polaris.core.Constant;
import com.polaris.core.config.ConfigFactory;
import com.polaris.core.config.reader.CofReaderFactory;
import com.polaris.core.util.EnvironmentUtil;
import com.polaris.core.util.FileUitl;
import com.polaris.core.util.NetUtils;
import com.polaris.core.util.StringUtil;

public class ConfSystemHandlerProvider {
	private static volatile String CONFIG_NAME = "application";
	private ConfSystemHandlerProvider() {}
	public static ConfSystemHandlerProvider INSTANCE = new ConfSystemHandlerProvider();
	private Properties properties = null;

	public void init(ConfCompositeProvider composite) {
		
		//application-properties
    	System.setProperty(Constant.FILE_ENCODING, Constant.UTF_CODE);
    	composite.put(ConfigFactory.SYSTEM, getProperties());
		if (StringUtil.isNotEmpty(System.getProperty(Constant.IP_ADDRESS))) {
			composite.put(ConfigFactory.SYSTEM, Constant.IP_ADDRESS, System.getProperty(Constant.IP_ADDRESS));
		} else {
			if (StringUtil.isEmpty(composite.getProperty(Constant.IP_ADDRESS))) {
				composite.put(ConfigFactory.SYSTEM, Constant.IP_ADDRESS, NetUtils.getLocalHost());
			}
		}
		
		//system-environment
		for (Map.Entry<String, String> entry : EnvironmentUtil.getSystemEnvironment().entrySet()) {
			if (StringUtil.isNotEmpty(entry.getValue())) {
				composite.put(ConfigFactory.SYSTEM, entry.getKey(), entry.getValue());
			}
		}
		
		//system-properties
		for (Map.Entry<Object, Object> entry : EnvironmentUtil.getSystemProperties().entrySet()) {
			if (entry.getValue() != null) {
				composite.put(ConfigFactory.SYSTEM, entry.getKey().toString(),entry.getValue().toString());
			}
		}
		
		
	}
	
	public Properties getProperties() {
		if (this.properties != null) {
			return this.properties;
		}
		
		//spring.config.location or project.config.name
    	Properties propeties = null;
    	String file = null;
    	String projectConfigLocation = System.getProperty(Constant.SPRING_CONFIG_LOCACTION);
    	if (StringUtil.isNotEmpty(projectConfigLocation)) {
    		file = projectConfigLocation;
    	} else if (StringUtil.isNotEmpty(System.getProperty(Constant.PROJECT_CONFIG_NAME))) {
    		file = System.getProperty(Constant.PROJECT_CONFIG_NAME);
    	}
    	if (StringUtil.isNotEmpty(file)) {
    		propeties = CofReaderFactory.get(file).getProperties(file,true,true);
    	} 
    	
		//folder-scan
    	if (propeties == null) {
    		for (String suffix : CofReaderFactory.SUPPORT_TYPE) {
        		file =  CONFIG_NAME + FileUitl.DOT + suffix;
        		propeties = CofReaderFactory.get(file).getProperties(file,true,false);
        		if (propeties != null) {
        			break;
        		}
        	}
        	
			//classpath-scan
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
