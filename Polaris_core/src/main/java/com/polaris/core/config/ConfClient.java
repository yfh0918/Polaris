package com.polaris.core.config;

import com.polaris.core.Constant;
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
		ConfHandlerProvider.INSTANCE.put(ConfigFactory.get(), key, value);
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
		String value = ConfHandlerProvider.INSTANCE.get(ConfigFactory.get(), key);
		if (value != null) {
			return value;
		}
		
		//扩展文件
		value = ConfHandlerProvider.INSTANCE.get(ConfigFactory.get(Config.EXTEND), key);
		if (value != null) {
			return value;
		}
		
		//全局
		value = ConfHandlerProvider.INSTANCE.get(ConfigFactory.get(Config.GLOBAL), key);
		if (value != null) {
			return value;
		}
		
		
		//默认值
		if (StringUtil.isNotEmpty(defaultVal)) {
			ConfHandlerProvider.INSTANCE.put(ConfigFactory.get(), key, defaultVal);
		}
		
		//返回默认值
		return defaultVal;
	}
	
	//在设置应用名称的时候启动各项参数载入
	public static String getAppName() {
		
		String appName = ConfHandlerProvider.INSTANCE.get(ConfigFactory.get(), Constant.PROJECT_NAME);
		if (StringUtil.isEmpty(appName)) {
			appName = ConfHandlerProvider.INSTANCE.get(ConfigFactory.get(), Constant.SPRING_BOOT_NAME);
		}
		return appName == null ? "" :appName;
	}

	public static String getConfigRegistryAddress() {
		String config = ConfHandlerProvider.INSTANCE.get(ConfigFactory.get(), Constant.CONFIG_REGISTRY_ADDRESS_NAME);
		return config == null ? "" :config;
	}
	public static String getNameSpace() {
		String namespace = ConfHandlerProvider.INSTANCE.get(ConfigFactory.get(), Constant.PROJECR_NAMESPACE_NAME);
		return namespace == null ? "" :namespace;
	}

	public static String getGroup() {
		String group = ConfHandlerProvider.INSTANCE.get(ConfigFactory.get(), Constant.PROJECR_GROUP_NAME);
		return group == null ? "" :group;
	}
	public static String getNamingRegistryAddress() {
		String naming = ConfHandlerProvider.INSTANCE.get(ConfigFactory.get(), Constant.NAMING_REGISTRY_ADDRESS_NAME);
		return naming == null ? "" :naming;
	}

}
