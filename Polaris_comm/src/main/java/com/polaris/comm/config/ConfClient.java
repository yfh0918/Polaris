package com.polaris.comm.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.polaris.comm.Constant;
import com.polaris.comm.util.LogUtil;
import com.polaris.comm.util.PropertyUtils;
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

	/**
	 * 更新或者增加配置信息(由conf_admin发起的更新)*
	 * 该方法只更新当前缓存，不更新zk。
     * 如果要更新zk请参见 {@link ConfZkClient#setPathDataByKey} 方法
	 * */
	public static void update(String key, String value) {
		if (StringUtil.isNotEmpty(key)) {
			if (cache.get(key)!=null) {
				logger.info(">>>>>>>>>> conf: 更新配置: [{}:{}]", new Object[]{key, value});
				cache.put(key, value);
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
		
		//config/application.properties配置最优先
		try {
			String propertyValue = PropertyUtils.readData(Constant.PROJECT_PROPERTY, key, false);
			if (propertyValue != null) {
				update(key, propertyValue);
				return propertyValue;
			}
		} catch (Exception ex) {
			//nothing
		}

		//扩展配置点获取信息 
		String data = ConfigHandlerProvider.getInstance().getKey(key, isWatch);
		if (data!=null) {
			update(key, data);//更新缓存
			return data;
		}

		// 最后兜底 config/application-xxx.properties
		try {
			String propertyValue = PropertyUtils.readData(Constant.CONFIG,"application", "properties", key, false);
			if (propertyValue != null) {
				update(key, propertyValue);
				return propertyValue;
			}
		} catch (Exception ex) {
			//nothing
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
	* 获取配置信息
	* @param 
	* @return 
	* @Exception 
	* @since 
	*/
	public static String[] getExtensionProperties() {
		String files = cache.get(Constant.PROJECT_EXTENSION_PROPERTIES);
		try {
			if (files == null) {
				files = PropertyUtils.readData(Constant.PROJECT_PROPERTY, Constant.PROJECT_EXTENSION_PROPERTIES, false);
			}
			if (StringUtil.isNotEmpty(files)) {
				cache.put(Constant.PROJECT_EXTENSION_PROPERTIES, files);
				return files.split(",");
			}
		} catch (Exception ex) {
			//nothing
		}
		return null;
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
