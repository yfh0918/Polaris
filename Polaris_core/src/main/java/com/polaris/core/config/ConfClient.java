package com.polaris.core.config;

import java.util.concurrent.atomic.AtomicBoolean;

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
	
	//初始化标志
	private static volatile AtomicBoolean initialized = new AtomicBoolean(false);
	
	//初始化操作
	public static void init() {
		
		//初始化
		if (!initialized.compareAndSet(false, true)) {
            return;
        }
		
    	//初始DEFAULT_CONFIG
		ConfHandlerProvider.initDefault();
		
		//初始化外部模块接入点
		ConfHandlerProvider.initEndPoint();
		
		//载入扩展文件
		ConfHandlerProvider.initHandler();
	}
	
	/**
	* 设置配置信息
	* @param 
	* @return 
	* @Exception 
	* @since 
	*/
	public static void set(String key, String value) {
		ConfHandlerEnum.DEFAULT.put(key, value);
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
		String value = ConfHandlerEnum.DEFAULT.get(key);
		if (value != null) {
			return value;
		}
		
		//扩展文件
		value = ConfHandlerEnum.EXTEND.get(key);
		if (value != null) {
			return value;
		}
		
		//全局
		value = ConfHandlerEnum.GLOBAL.get(key);
		if (value != null) {
			return value;
		}
		
		
		//默认值
		if (StringUtil.isNotEmpty(defaultVal)) {
			ConfHandlerEnum.DEFAULT.put(key, defaultVal);
		}
		
		//返回默认值
		return defaultVal;
	}
	
	//在设置应用名称的时候启动各项参数载入
	public static String getAppName() {
		
		String appName = ConfHandlerEnum.DEFAULT.get(Constant.PROJECT_NAME);
		if (StringUtil.isEmpty(appName)) {
			appName = ConfHandlerEnum.DEFAULT.get(Constant.SPRING_BOOT_NAME);
		}
		return appName == null ? "" :appName;
	}

	public static String getConfigRegistryAddress() {
		String config = ConfHandlerEnum.DEFAULT.get(Constant.CONFIG_REGISTRY_ADDRESS_NAME);
		return config == null ? "" :config;
	}
	public static String getNameSpace() {
		String namespace = ConfHandlerEnum.DEFAULT.get(Constant.PROJECR_NAMESPACE_NAME);
		return namespace == null ? "" :namespace;
	}

	public static String getGroup() {
		String group = ConfHandlerEnum.DEFAULT.get(Constant.PROJECR_GROUP_NAME);
		return group == null ? "" :group;
	}
	public static String getNamingRegistryAddress() {
		String naming = ConfHandlerEnum.DEFAULT.get(Constant.NAMING_REGISTRY_ADDRESS_NAME);
		return naming == null ? "" :naming;
	}

}
