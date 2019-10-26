package com.polaris.core.config;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.core.Constant;
import com.polaris.core.util.EnvironmentUtil;
import com.polaris.core.util.NetUtils;
import com.polaris.core.util.PropertyUtils;
import com.polaris.core.util.StringUtil;

/**
*
* 项目名称：Polaris_core
* 类名称：ConfClient
* 类描述：
* 创建人：yufenghua
* 创建时间：2018年5月9日 上午9:15:12
* 修改人：yufenghua
* 修改时间：2018年5月9日 上午9:15:12
* 修改备注：
* @version
*
*/
public abstract class ConfClient {
	private static final Logger logger = LoggerFactory.getLogger(ConfClient.class);
	
	//初始化操作
	public static void init() {
		
    	//初始DEFAULT_CONFIG
		loadDefaultConfig();
		
		//载入扩展文件
		String[] extendProperties = ConfHandlerSupport.getExtensionProperties();
		if (extendProperties != null) {
			for (String file : extendProperties) {
				logger.info("{} loading start",file);
				ConfigHandlerProvider.loadConfig(file);//载入缓存
				logger.info("{} loading end",file);
			}
		}
		
		//载入全局文件
		String[] globalProperties = ConfHandlerSupport.getGlobalProperties();
		if (globalProperties != null) {
			for (String file : globalProperties) {
				logger.info("{} loading start",file);
				ConfigHandlerProvider.loadConfig(file);
				logger.info("{} loading start",file);
			}
		}
	}
	public static void loadDefaultConfig() {
		
    	// 启动字符集
    	System.setProperty(Constant.FILE_ENCODING, Constant.UTF_CODE);

		// 设置文件
    	String projectConfigLocation = System.getProperty(Constant.PROJECT_CONFIG_NAME);
    	if (StringUtil.isNotEmpty(projectConfigLocation)) {
    		Constant.DEFAULT_CONFIG_NAME = projectConfigLocation;
    	}
    	
    	logger.info("{} loading start",Constant.DEFAULT_CONFIG_NAME);
    	//获取DEFAULT_CONFIG_NAME
		String content = PropertyUtils.getPropertiesFileContent(Constant.DEFAULT_CONFIG_NAME);
		ConfigHandlerProvider.cacheConfig(Constant.DEFAULT_CONFIG_NAME, content, false);
		if (StringUtil.isNotEmpty(System.getProperty(Constant.IP_ADDRESS))) {
	    	ConfigHandlerProvider.updateValue(Constant.IP_ADDRESS, System.getProperty(Constant.IP_ADDRESS), Constant.DEFAULT_CONFIG_NAME);
		} else {
	    	ConfigHandlerProvider.updateValue(Constant.IP_ADDRESS, NetUtils.getLocalHost(), Constant.DEFAULT_CONFIG_NAME);
		}
		logger.info("{} loading end",Constant.DEFAULT_CONFIG_NAME);
		
		//查询所有systemenv
    	logger.info("systemEnv loading start");
		for (Map.Entry<String, String> entry : EnvironmentUtil.getSystemEnvironment().entrySet()) {
			if (StringUtil.isNotEmpty(entry.getValue())) {
				ConfigHandlerProvider.updateValue(entry.getKey(), entry.getValue(), Constant.DEFAULT_CONFIG_NAME);
			}
		}
    	logger.info("systemEnv loading end");
		
		//查询所有systemProperties
    	logger.info("systemProperties loading start");
		for (Map.Entry<String, String> entry : EnvironmentUtil.getSystemProperties().entrySet()) {
			if (StringUtil.isNotEmpty(entry.getValue())) {
				ConfigHandlerProvider.updateValue(entry.getKey(), entry.getValue(), Constant.DEFAULT_CONFIG_NAME);
			}
		}
		logger.info("systemProperties loading end");
	}
	
	/**
	* 获取配置信息
	* @param 
	* @return 
	* @Exception 
	* @since 
	*/
	public static String get(String key) {
		return get(key, "");
	}
	public static String get(String key, String defaultVal) {
		
		//application.properties
		String value = ConfigHandlerProvider.getValue(key,Constant.DEFAULT_CONFIG_NAME);
		if (value != null) {
			return value;
		}
		
		//扩展文件
		String[] allProperties = ConfHandlerSupport.getExtensionProperties();
		if (allProperties != null) {
			for (String file : allProperties) {
				value = ConfigHandlerProvider.getValue(key, file);
				if (value != null) {
					return value;
				}
			}
		}
		
		//全局
		String[] globalProperties = ConfHandlerSupport.getGlobalProperties();
		if (globalProperties != null) {
			for (String file : globalProperties) {
				value = ConfigHandlerProvider.getValue(key, file);
				if (value != null) {
					return value;
				}
			}
		}
		
		//默认值
		if (StringUtil.isNotEmpty(defaultVal)) {
			ConfigHandlerProvider.updateValue(key, defaultVal, Constant.DEFAULT_CONFIG_NAME);
		}
		
		//返回默认值
		return defaultVal;
	}
	
	//在设置应用名称的时候启动各项参数载入
	public static String getAppName() {
		
		String appName = ConfigHandlerProvider.getValue(Constant.PROJECT_NAME, Constant.DEFAULT_CONFIG_NAME);
		if (StringUtil.isEmpty(appName)) {
			appName = ConfigHandlerProvider.getValue(Constant.SPRING_BOOT_NAME, Constant.DEFAULT_CONFIG_NAME);
		}
		return appName == null ? "" :appName;
	}

	//获取配置文件
	public static String getConfigValue(String fileName) {
		return ConfigHandlerProvider.getConfig(fileName);
	}

	//增加文件是否修改的监听
	public static void addListener(String fileName, ConfListener listener) {
		ConfigHandlerProvider.addListener(fileName, listener);
	}

	public static String getConfigRegistryAddress() {
		String config = ConfigHandlerProvider.getValue(Constant.CONFIG_REGISTRY_ADDRESS_NAME, Constant.DEFAULT_CONFIG_NAME);
		return config == null ? "" :config;
	}
	public static String getNameSpace() {
		String namespace = ConfigHandlerProvider.getValue(Constant.PROJECR_NAMESPACE_NAME, Constant.DEFAULT_CONFIG_NAME);
		return namespace == null ? "" :namespace;
	}

	public static String getGroup() {
		String group = ConfigHandlerProvider.getValue(Constant.PROJECR_GROUP_NAME, Constant.DEFAULT_CONFIG_NAME);
		return group == null ? "" :group;
	}
	public static String getNamingRegistryAddress() {
		String naming = ConfigHandlerProvider.getValue(Constant.NAMING_REGISTRY_ADDRESS_NAME, Constant.DEFAULT_CONFIG_NAME);
		return naming == null ? "" :naming;
	}

}
