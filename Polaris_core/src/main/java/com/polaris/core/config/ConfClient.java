package com.polaris.core.config;

import com.polaris.core.Constant;
import com.polaris.core.config.provider.ConfCompositeProvider;

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
		ConfCompositeProvider.INSTANCE.putProperty(key, value);
	}
	
	/**
	* 获取配置信息
	* @param 
	* @return 
	* @Exception 
	* @since 
	*/
	public static String get(String key, String... defaultVal) {
		return ConfCompositeProvider.INSTANCE.getProperty(key,defaultVal);
	}
	
	public static String getAppName() {
		return get(Constant.PROJECT_NAME,get(Constant.SPRING_BOOT_NAME));
	}
	public static String getConfigRegistryAddress() {
		return get(Constant.CONFIG_REGISTRY_ADDRESS_NAME);
	}
	public static String getNameSpace() {
		return get(Constant.PROJECR_NAMESPACE_NAME);
	}
	public static String getGroup() {
		return get(Constant.PROJECR_GROUP_NAME);
	}
	public static String getNamingRegistryAddress() {
		return get(Constant.NAMING_REGISTRY_ADDRESS_NAME);
	}

}
