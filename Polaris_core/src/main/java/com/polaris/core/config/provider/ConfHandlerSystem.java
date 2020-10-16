package com.polaris.core.config.provider;

import java.util.Properties;

import com.polaris.core.Constant;
import com.polaris.core.config.ConfHandlerListener;
import com.polaris.core.config.ConfigChangeListener;
import com.polaris.core.config.reader.launcher.ConfLauncherReaderStrategy;
import com.polaris.core.exception.ConfigException;
import com.polaris.core.util.EnvironmentUtil;
import com.polaris.core.util.NetUtils;
import com.polaris.core.util.StringUtil;

public class ConfHandlerSystem extends ConfHandlerExtension {
	private static volatile String CONFIG_NAME = "application";
    private static Properties properties;
    private static String systemConfigName;
	public ConfHandlerSystem(ConfigChangeListener... configListeners) {
	    super(configListeners);
        notifier.notify(Constant.DEFAULT_GROUP, systemConfigName, null,getProperties(),configChangeListeners);
	}

	public static Properties getProperties() {
		if (properties != null) {
			return properties;
		}
		synchronized(ConfHandlerSystem.class) {
			//encode
	    	System.setProperty(Constant.FILE_ENCODING, Constant.UTF_CODE);

	    	//spring.config.location or project.config.name
	    	Properties localProperties = null;
	    	String file = null;
	    	String projectConfigLocation = System.getProperty(Constant.SPRING_CONFIG_LOCACTION);
	    	if (StringUtil.isNotEmpty(projectConfigLocation)) {
	    		file = projectConfigLocation;
	    	} else if (StringUtil.isNotEmpty(System.getProperty(Constant.PROJECT_CONFIG_NAME))) {
	    		file = System.getProperty(Constant.PROJECT_CONFIG_NAME);
	    	}
	    	if (StringUtil.isNotEmpty(file)) {
	    	    systemConfigName = file;
	    	    localProperties = ConfLauncherReaderStrategy.INSTANCE.getProperties(file);
	    	} 
	    	
			//folder-scan
	    	if (localProperties == null) {
	    	    systemConfigName = CONFIG_NAME;
	    	    localProperties = ConfLauncherReaderStrategy.INSTANCE.getProperties(CONFIG_NAME);
	    	}
	    	
	    	if (StringUtil.isNotEmpty(System.getProperty(Constant.IP_ADDRESS))) {
	    	    localProperties.put(Constant.IP_ADDRESS, System.getProperty(Constant.IP_ADDRESS));
			} else {
				if (StringUtil.isEmpty(localProperties.getProperty(Constant.IP_ADDRESS))) {
				    localProperties.put(Constant.IP_ADDRESS, NetUtils.getLocalHost());
				}
			}
			
			//system-environment
	    	localProperties.putAll(EnvironmentUtil.getSystemEnvironment());
			
			//system-properties
	    	localProperties.putAll(EnvironmentUtil.getSystemProperties());
	     	properties = localProperties;
		}
    	return properties;
	}
	
	@Override
	public void listen(String fileName, String group, ConfHandlerListener listener) {
	    throw new ConfigException("Not supported for listen method");
	}
    
	@Override
	public String get(String fileName, String group) {
	    throw new ConfigException("Not supported for get method");
	}
	
	@Override
    public String getAndListen(String fileName, String group, ConfHandlerListener listener) {
        throw new ConfigException("Not supported for getAndListen method");
    }
}
