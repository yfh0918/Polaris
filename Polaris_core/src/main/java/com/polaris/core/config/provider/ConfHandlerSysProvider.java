package com.polaris.core.config.provider;

import java.util.Map;
import java.util.Properties;

import com.polaris.core.Constant;
import com.polaris.core.config.Config.Opt;
import com.polaris.core.config.Config.Type;
import com.polaris.core.config.ConfigFactory;
import com.polaris.core.config.ConfigListener;
import com.polaris.core.config.reader.ConfReaderStrategyFactory;
import com.polaris.core.util.EnvironmentUtil;
import com.polaris.core.util.NetUtils;
import com.polaris.core.util.StringUtil;

public class ConfHandlerSysProvider implements ConfHandlerProvider{
	private static volatile String CONFIG_NAME = "application";
	private static String SYSTEM_SEQUENCE = "system";
	public static ConfHandlerSysProvider INSTANCE = new ConfHandlerSysProvider();
	private ConfHandlerSysProvider() {}
	private Properties properties = null;
	
	@Override
	public void init(ConfigListener configListener) {
		for (Map.Entry<Object, Object> entry : getProperties().entrySet()) {
			configListener.onChange(SYSTEM_SEQUENCE, entry.getKey(), entry.getValue(), Opt.ADD);
		}
		ConfigFactory.get(Type.SYS).put(Type.SYS.name(), getProperties());
		configListener.onComplete(SYSTEM_SEQUENCE);
	}
	
	public Properties getProperties() {
		if (this.properties != null) {
			return this.properties;
		}
		synchronized(this) {
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
	    		propeties = ConfReaderStrategyFactory.get().getProperties(file);
	    	} 
	    	
			//folder-scan
	    	if (propeties == null) {
	    		propeties = ConfReaderStrategyFactory.get().getProperties(CONFIG_NAME);
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
		}
    	return this.properties;
	}
}
