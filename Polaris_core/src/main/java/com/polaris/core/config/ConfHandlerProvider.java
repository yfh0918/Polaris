package com.polaris.core.config;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.core.Constant;
import com.polaris.core.config.value.AutoUpdateConfigChangeListener;
import com.polaris.core.util.EnvironmentUtil;
import com.polaris.core.util.NetUtils;
import com.polaris.core.util.PropertyUtils;
import com.polaris.core.util.SpringUtil;
import com.polaris.core.util.StringUtil;


public class ConfHandlerProvider extends ConfHandlerProviderAbs {

	private static final Logger logger = LoggerFactory.getLogger(ConfHandlerProvider.class);
	
	/**
     * 单实例
     */
    public static final ConfHandlerProvider INSTANCE = new ConfHandlerProvider();
    private ConfHandlerProvider() {}
    
    @Override
    protected void initDefault() {
    	// 启动字符集
    	System.setProperty(Constant.FILE_ENCODING, Constant.UTF_CODE);

		// 设置文件
    	String projectConfigLocation = System.getProperty(Constant.SPRING_CONFIG_LOCACTION);
    	if (StringUtil.isNotEmpty(projectConfigLocation)) {
    		Constant.DEFAULT_CONFIG_NAME = projectConfigLocation;
    	} else if (StringUtil.isNotEmpty(System.getProperty(Constant.PROJECT_CONFIG_NAME))) {
    		Constant.DEFAULT_CONFIG_NAME = System.getProperty(Constant.PROJECT_CONFIG_NAME);
    	}
    	
    	logger.info("{} loading start",Constant.DEFAULT_CONFIG_NAME);
    	//获取DEFAULT_CONFIG_NAME
		ConfHandlerEnum.DEFAULT.put(PropertyUtils.getPropertiesFileContent(Constant.DEFAULT_CONFIG_NAME));
		if (StringUtil.isNotEmpty(System.getProperty(Constant.IP_ADDRESS))) {
			ConfHandlerEnum.DEFAULT.put(Constant.IP_ADDRESS, System.getProperty(Constant.IP_ADDRESS));
		} else {
			ConfHandlerEnum.DEFAULT.put(Constant.IP_ADDRESS, NetUtils.getLocalHost());
		}
		logger.info("{} loading end",Constant.DEFAULT_CONFIG_NAME);
		
		//查询所有systemenv
    	logger.info("systemEnv loading start");
		for (Map.Entry<String, String> entry : EnvironmentUtil.getSystemEnvironment().entrySet()) {
			if (StringUtil.isNotEmpty(entry.getValue())) {
				ConfHandlerEnum.DEFAULT.put(entry.getKey(), entry.getValue());
			}
		}
    	logger.info("systemEnv loading end");
		
		//查询所有systemProperties
    	logger.info("systemProperties loading start");
		for (Map.Entry<String, String> entry : EnvironmentUtil.getSystemProperties().entrySet()) {
			if (StringUtil.isNotEmpty(entry.getValue())) {
				ConfHandlerEnum.DEFAULT.put(entry.getKey(), entry.getValue());
			}
		}
		logger.info("systemProperties loading end");
    }
    
	@Override
    protected void initHandler(String type) {
    	
		//获取配置
		ConfHandlerEnum configEnum = ConfHandlerEnum.getConfig(type);
		
		//获取配置文件
		String[] properties = ConfHandlerSupport.getProperties(type);
		
		//处理文件
		if (properties != null) {
			for (String file : properties) {
				
				//载入配置到缓存
				logger.info("{} loading start",file);
		    	configEnum.put(get(file,ConfHandlerSupport.getGroup(type)));
				
		    	//增加监听
		    	listen(file, ConfHandlerSupport.getGroup(type), new ConfListener() {
					@Override
					public void receive(String config) {
						configEnum.put(config);
				    	SpringUtil.getBean(AutoUpdateConfigChangeListener.class).onChange(configEnum.get());//监听配置
					}
				});
				logger.info("{} loading end",file);
			}
		}
    }
    
	public String get(String fileName) {
		return get(fileName, ConfClient.getAppName());
	}
	
	public void listen(String fileName, ConfListener listerner) {
		listen(fileName, ConfClient.getAppName(), listerner);
	}

}
