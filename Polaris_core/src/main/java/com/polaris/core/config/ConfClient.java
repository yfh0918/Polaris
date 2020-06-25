package com.polaris.core.config;

import java.util.Properties;

import com.polaris.core.Constant;
import com.polaris.core.config.provider.ConfHandlerComposite;

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
	* 初始化
	* @param 
	* @return 
	* @Exception 
	* @since 
	*/
	public static void init() {
		ConfHandlerComposite.INSTANCE.init();
	}
	
	/**
	* 设置配置信息
	* @param 
	* @return 
	* @Exception 
	* @since 
	*/
	public static void set(String key, String value) {
		ConfHandlerComposite.INSTANCE.putProperty(key, value);
	}
	
	/**
	* 获取配置信息
	* @param 
	* @return 
	* @Exception 
	* @since 
	*/
	public static String get(String key, String... defaultVal) {
		return ConfHandlerComposite.INSTANCE.getProperty(key,defaultVal);
	}
	
    /**
    * 获取全体配置
    * @param 
    * @return 
    * @Exception 
    * @since 
    */
    public static Properties get() {
        return ConfHandlerComposite.INSTANCE.getProperties();
    }
	
	/**
	* 应用名称
	* @param 
	* @return 
	* @Exception 
	* @since 
	*/
	public static String getAppName() {
		return get(Constant.PROJECT_NAME,get(Constant.SPRING_BOOT_NAME));
	}
	
	/**
	* 注册中心地址
	* @param 
	* @return 
	* @Exception 
	* @since 
	*/
	public static String getNamingRegistryAddress() {
		return get(Constant.NAMING_REGISTRY_ADDRESS_NAME);
	}
	
	/**
	* 配置中心地址
	* @param 
	* @return 
	* @Exception 
	* @since 
	*/
	public static String getConfigRegistryAddress() {
		return get(Constant.CONFIG_REGISTRY_ADDRESS_NAME);
	}
	
	/**
	* 命名空间 可以用于区分开发 环境，测试环境，生产环境
	* @param 
	* @return 
	* @Exception 
	* @since 
	*/
	public static String getNameSpace() {
		return get(Constant.PROJECR_NAMESPACE_NAME);
	}
	
	/**
	* 集群分组，介于namespace和AppName中间
	* @param 
	* @return 
	* @Exception 
	* @since 
	*/
	public static String getGroup() {
		return get(Constant.PROJECR_GROUP_NAME);
	}

}
