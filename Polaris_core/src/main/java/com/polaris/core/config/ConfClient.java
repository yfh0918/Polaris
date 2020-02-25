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
		ConfCompositeProvider.INSTANCE.put(ConfigFactory.get()[0], key, value);
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
			String value = ConfCompositeProvider.INSTANCE.get(config, key);
			if (value != null) {
				return value;
			}
		}
		
		//默认值
		if (StringUtil.isNotEmpty(defaultVal)) {
			ConfCompositeProvider.INSTANCE.put(ConfigFactory.get()[0], key, defaultVal);
		}
		
		//返回默认值
		return defaultVal;
	}
	
	//在设置应用名称的时候启动各项参数载入
	public static String getAppName() {
		
		String appName = ConfCompositeProvider.INSTANCE.get(ConfigFactory.get()[0], Constant.PROJECT_NAME);
		if (StringUtil.isEmpty(appName)) {
			appName = ConfCompositeProvider.INSTANCE.get(ConfigFactory.get()[0], Constant.SPRING_BOOT_NAME);
		}
		return appName == null ? "" :appName;
	}

	public static String getConfigRegistryAddress() {
		String config = ConfCompositeProvider.INSTANCE.get(ConfigFactory.get()[0], Constant.CONFIG_REGISTRY_ADDRESS_NAME);
		return config == null ? "" :config;
	}
	public static String getNameSpace() {
		String namespace = ConfCompositeProvider.INSTANCE.get(ConfigFactory.get()[0], Constant.PROJECR_NAMESPACE_NAME);
		return namespace == null ? "" :namespace;
	}

	public static String getGroup() {
		String group = ConfCompositeProvider.INSTANCE.get(ConfigFactory.get()[0], Constant.PROJECR_GROUP_NAME);
		return group == null ? "" :group;
	}
	public static String getNamingRegistryAddress() {
		String naming = ConfCompositeProvider.INSTANCE.get(ConfigFactory.get()[0], Constant.NAMING_REGISTRY_ADDRESS_NAME);
		return naming == null ? "" :naming;
	}

}
