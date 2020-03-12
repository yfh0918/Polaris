package com.polaris.core.config.provider;

import java.util.Map;
import java.util.Properties;

import com.polaris.core.Constant;
import com.polaris.core.config.reader.ConfReaderFactory;
import com.polaris.core.util.EnvironmentUtil;
import com.polaris.core.util.FileUtil;
import com.polaris.core.util.NetUtils;
import com.polaris.core.util.StringUtil;

public class ConfSystemHandlerProvider {
	private static volatile String CONFIG_NAME = "application";
	private ConfSystemHandlerProvider() {}
	public static ConfSystemHandlerProvider INSTANCE = new ConfSystemHandlerProvider();
	private Properties properties = null;

	public void init(ConfCompositeProvider composite) {
		for (Map.Entry<Object, Object> entry : getProperties().entrySet()) {
			composite.putProperty(entry.getKey(), entry.getValue());
		}
	}
	
	public Properties getProperties() {
		if (this.properties != null) {
			return this.properties;
		}
		
		//encode
    	System.setProperty(Constant.FILE_ENCODING, Constant.UTF_CODE);

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
    		propeties = ConfReaderFactory.get(file).getProperties(file,true,true);
    	} 
    	
		//folder-scan
    	if (propeties == null) {
    		for (String suffix : ConfReaderFactory.SUPPORT_TYPE) {
        		file =  CONFIG_NAME + FileUtil.DOT + suffix;
        		propeties = ConfReaderFactory.get(file).getProperties(file,true,false);
        		if (propeties != null) {
        			break;
        		}
        	}
        	
			//classpath-scan
    		if (propeties == null) {
    			for (String suffix : ConfReaderFactory.SUPPORT_TYPE) {
            		file =  CONFIG_NAME + FileUtil.DOT + suffix;
            		propeties = ConfReaderFactory.get(file).getProperties(file,false,true);
            		if (propeties != null) {
            			break;
            		}
            	}
    		}
    	}
    	
    	if (StringUtil.isNotEmpty(System.getProperty(Constant.IP_ADDRESS))) {
    		propeties.put(Constant.IP_ADDRESS, System.getProperty(Constant.IP_ADDRESS));
		} else {
			if (StringUtil.isEmpty(propeties.getProperty(Constant.IP_ADDRESS))) {
				propeties.put(Constant.IP_ADDRESS, NetUtils.getLocalHost());
			}
		}
		
		//system-environment
    	propeties.putAll(EnvironmentUtil.getSystemEnvironment());
		
		//system-properties
		propeties.putAll(EnvironmentUtil.getSystemProperties());
     	this.properties = propeties;
    	return propeties;
	}
	

}
