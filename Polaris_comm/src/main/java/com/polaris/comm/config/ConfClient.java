package com.polaris.comm.config;

import java.util.List;

import com.polaris.comm.Constant;
import com.polaris.comm.util.StringUtil;

/**
*
* 项目名称：Polaris_comm
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
public class ConfClient {
	
	/**
	* 获取配置信息
	* @param 
	* @return 
	* @Exception 
	* @since 
	*/
	public static String get(String key) {
		return get(key, "", true);
	}
	public static String get(String key, boolean isWatch) {
		return get(key, "", isWatch);
	}
	public static String get(String key, String defaultValue) {
		return get(key, defaultValue, true);
	}
	@SuppressWarnings("static-access")
	public static String get(String key, String defaultVal, boolean isWatch) {
		
		//获取所有文件
		List<String> allProperties = ConfigHandlerProvider.getInstance().getAllProperties();
		for (String file : allProperties) {
			String value = ConfigHandlerProvider.getInstance().getValue(key, file, isWatch);
			if (value != null) {
				return value;
			}
		}
		
		//默认值
		if (StringUtil.isNotEmpty(defaultVal)) {
			ConfigHandlerProvider.getInstance().updateCache(key, defaultVal, Constant.DEFAULT_CONFIG_NAME);
		}
		
		//返回默认值
		return defaultVal;
	}
	

	//增加文件是否修改的监听
	public static void addListener(String fileName, ConfListener listener) {
		ConfigHandlerProvider.getInstance().addListener(fileName, listener);
	}

	@SuppressWarnings("static-access")
	public static void setAppName(String inputAppName) {
		ConfigHandlerProvider.getInstance().updateCache(Constant.PROJECT_NAME, inputAppName, Constant.DEFAULT_CONFIG_NAME);
	}
	public static String getAppName() {
		return get(Constant.PROJECT_NAME) == null ? "" : get(Constant.PROJECT_NAME);
	}
	public static String getConfigRegistryAddress() {
		return System.getProperty(Constant.CONFIG_REGISTRY_ADDRESS_NAME);
	}
	public static String getNameSpace() {
		return get(Constant.PROJECR_NAMESPACE_NAME) == null ? "" : get(Constant.PROJECR_NAMESPACE_NAME);
	}
	public static String getCluster() {
		return get(Constant.PROJECR_CLUSTER_NAME) == null ? "" : get(Constant.PROJECR_CLUSTER_NAME);
	}
	public static String getEnv() {
		return get(Constant.PROJECT_ENV_NAME) == null ? "" : get(Constant.PROJECT_ENV_NAME);
	}
	
}
