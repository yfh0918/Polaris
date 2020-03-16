package com.polaris.core.config.provider;

import java.util.Map;
import java.util.Properties;

import com.polaris.core.Constant;
import com.polaris.core.config.Config;
import com.polaris.core.config.Config.Opt;
import com.polaris.core.config.ConfigChangeException;
import com.polaris.core.config.ConfigFactory;
import com.polaris.core.config.ConfigListener;
import com.polaris.core.config.reader.ConfReaderFactory;
import com.polaris.core.util.EnvironmentUtil;
import com.polaris.core.util.FileUtil;
import com.polaris.core.util.NetUtils;
import com.polaris.core.util.StringUtil;

public class ConfSystemHandlerProvider implements ConfHandlerProvider{
	private static volatile String CONFIG_NAME = "application";
	private static String SYSTEM_SEQUENCE = "system";
	public static ConfSystemHandlerProvider INSTANCE = new ConfSystemHandlerProvider();
	private ConfSystemHandlerProvider() {}
	private Properties properties = null;
	
	@Override
	public void init(ConfigListener configListener) {
		boolean isUpdate = false;
		for (Map.Entry<Object, Object> entry : getProperties().entrySet()) {
			try {
				configListener.onChange(SYSTEM_SEQUENCE, ConfigFactory.get(Config.SYSTEM), Config.SYSTEM, entry.getKey(), entry.getValue(), Opt.ADD);
				isUpdate = true;
			} catch (ConfigChangeException ex) {
				//nothing
			}
		}
		ConfigFactory.get(Config.SYSTEM).put(Config.SYSTEM, getProperties());
		if (isUpdate) {
			configListener.onComplete(SYSTEM_SEQUENCE);
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
