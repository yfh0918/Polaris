package com.polaris.core.config;

import java.util.Properties;

import com.polaris.core.Constant;
import com.polaris.core.config.provider.ConfCompositeProvider;
import com.polaris.core.config.provider.ConfSystemHandlerProvider;
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
	
	/**
	* 设置配置信息
	* @param 
	* @return 
	* @Exception 
	* @since 
	*/
	public static void set(String key, String value) {
		ConfCompositeProvider.INSTANCE.put(ConfigFactory.DEFAULT, ConfSystemHandlerProvider.FILE, key, value);
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
		
		//获取配置
		for (Config config : ConfigFactory.get()) {
			String value = ConfCompositeProvider.INSTANCE.get(config).getProperty(key);
			if (value != null) {
				return value;
			}
		}
		
		//默认值
		if (StringUtil.isNotEmpty(defaultVal)) {
			set(key, defaultVal);
		}
		
		//返回默认值
		return defaultVal;
	}
	public static String get(String file, String key, String defaultVal) {
		
		//获取配置
		for (Config config : ConfigFactory.get()) {
			String value = ConfCompositeProvider.INSTANCE.get(config, file).getProperty(key);
			if (value != null) {
				return value;
			}
		}
		
		//默认值
		if (StringUtil.isNotEmpty(defaultVal)) {
			set(key, defaultVal);
		}
		
		//返回默认值
		return defaultVal;
	}
	public static String get(String type ,String file, String key, String defaultVal) {
		
		//获取配置
		Config config = ConfigFactory.get(type);
		String value = ConfCompositeProvider.INSTANCE.get(config, file).getProperty(key);
		if (value != null) {
			return value;
		}
		
		//默认值
		if (StringUtil.isNotEmpty(defaultVal)) {
			set(key, defaultVal);
		}
		
		//返回默认值
		return defaultVal;
	}
	
	/**
	* 获取配置文件
	* @param 
	* @return 
	* @Exception 
	* @since 
	*/
	public static Properties getProperties(String type ,String file) {
		Config config = ConfigFactory.get(type);
		return ConfCompositeProvider.INSTANCE.get(config, file);
	}
	public static Properties getProperties(String file) {
		for (Config config : ConfigFactory.get()) {
			Properties properties = ConfCompositeProvider.INSTANCE.get(config, file);
			if (properties != null) {
				return properties;
			}
		}
		return null;
	}
	
	//在设置应用名称的时候启动各项参数载入
	public static String getAppName() {
		
		String appName = ConfCompositeProvider.INSTANCE.get(ConfigFactory.DEFAULT, ConfSystemHandlerProvider.FILE, Constant.PROJECT_NAME);
		if (StringUtil.isEmpty(appName)) {
			appName = ConfCompositeProvider.INSTANCE.get(ConfigFactory.DEFAULT, ConfSystemHandlerProvider.FILE, Constant.SPRING_BOOT_NAME);
		}
		return appName == null ? "" :appName;
	}

	public static String getConfigRegistryAddress() {
		String config = ConfCompositeProvider.INSTANCE.get(ConfigFactory.DEFAULT, ConfSystemHandlerProvider.FILE, Constant.CONFIG_REGISTRY_ADDRESS_NAME);
		return config == null ? "" :config;
	}
	public static String getNameSpace() {
		String namespace = ConfCompositeProvider.INSTANCE.get(ConfigFactory.DEFAULT, ConfSystemHandlerProvider.FILE, Constant.PROJECR_NAMESPACE_NAME);
		return namespace == null ? "" :namespace;
	}

	public static String getGroup() {
		String group = ConfCompositeProvider.INSTANCE.get(ConfigFactory.DEFAULT, ConfSystemHandlerProvider.FILE, Constant.PROJECR_GROUP_NAME);
		return group == null ? "" :group;
	}
	public static String getNamingRegistryAddress() {
		String naming = ConfCompositeProvider.INSTANCE.get(ConfigFactory.DEFAULT, ConfSystemHandlerProvider.FILE, Constant.NAMING_REGISTRY_ADDRESS_NAME);
		return naming == null ? "" :naming;
	}

}
