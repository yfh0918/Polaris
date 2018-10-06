package com.polaris.comm.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.polaris.comm.Constant;
import com.polaris.comm.util.LogUtil;
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
	private static final LogUtil logger = LogUtil.getInstance(ConfClient.class);
	private static Map<String, String> cache = new ConcurrentHashMap<>();

	public static void update(String key, String value) {
		if (StringUtil.isNotEmpty(key)) {
			String content = cache.get(key);
			if (content!=null) {
				if (!content.equals(value)) {
					logger.info(">>>>>>>>>> conf: 更新配置: [{}:{}]", new Object[]{key, value});
					cache.put(key, value);
				}
			} else {
				logger.info(">>>>>>>>>> conf: 初始化配置: [{}:{}]", new Object[]{key, value});
				cache.put(key, value);
			}
		}
	}

	
	/**
	* 获取配置信息
	* @param 
	* @return 
	* @Exception 
	* @since 
	*/
	public static String get(String key, String defaultVal, boolean isWatch) {
		
		//从缓存中存在直接缓存中获取
		String value = cache.get(key);
		if (value != null) {
			return value;
		}
		
		//扩展配置点获取信息 
		String data = ConfigHandlerProvider.getInstance().getValue(key, isWatch);
		if (data!=null) {
			update(key, data);//更新缓存
			return data;
		}

		//默认值
		if (StringUtil.isNotEmpty(defaultVal)) {
			update(key, defaultVal);
		} else {
			logger.warn(">>>>>>>>>> conf: 参数{}获取失败", key);
		}
		
		return defaultVal;
	}
	
	/**
	* 获取配置文件内容
	* @param 
	* @return 
	* @Exception 
	* @since 
	*/
	public static String getFileContent(String fileName) {
		return ConfigHandlerProvider.getInstance().getFileContent(fileName);
	}
	//增加文件是否修改的监听
	public static void addListener(String fileName, ConfListener listener) {
		ConfigHandlerProvider.getInstance().addListener(fileName, listener);
	}

	
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
	
	/**
	* 删除配置信息(由conf_admin发起的删除)
	* @param 
	* @return 
	* @Exception 
	* @since 
	*/
	public static boolean remove(String key) {
		if (StringUtil.isNotEmpty(key)) {
			if (cache.get(key)!=null) {
				logger.info(">>>>>>>>>> conf: 删除配置：key ", key);
				cache.remove(key);
				return true;
			}
		}
		return false;
	}
	
	public static void setAppName(String inputAppName) {
		cache.put(Constant.PROJECT_NAME, inputAppName);
	}
	public static String getAppName() {
		return cache.get(Constant.PROJECT_NAME) == null ? "" : cache.get(Constant.PROJECT_NAME);
	}
	public static String getConfigRegistryAddress() {
		return cache.get(Constant.CONFIG_REGISTRY_ADDRESS_NAME) == null ? "" : cache.get(Constant.CONFIG_REGISTRY_ADDRESS_NAME);
	}
	public static String getNameSpace() {
		return cache.get(Constant.PROJECR_NAMESPACE_NAME) == null ? "" : cache.get(Constant.PROJECR_NAMESPACE_NAME);
	}
	public static String getCluster() {
		return cache.get(Constant.PROJECR_CLUSTER_NAME) == null ? "" : cache.get(Constant.PROJECR_CLUSTER_NAME);
	}
	public static String getEnv() {
		return cache.get(Constant.PROJECT_ENV_NAME) == null ? "" : cache.get(Constant.PROJECT_ENV_NAME);
	}
}
