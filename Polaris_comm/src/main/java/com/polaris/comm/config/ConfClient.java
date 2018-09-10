package com.polaris.comm.config;

import java.util.List;

import com.polaris.comm.Constant;
import com.polaris.comm.util.LogUtil;
import com.polaris.comm.util.PropertyUtils;
import com.polaris.comm.util.StringUtil;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

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
	private static String appName;

	private static Cache cache;
	static {
		CacheManager manager = CacheManager.create();	// default use ehcche.xml under src
		cache = new Cache(ConfClient.class.getName(), 10000, false, true, 1800, 1800);
		manager.addCache(cache);
	}

	/**
	 * 更新或者增加配置信息(由conf_admin发起的更新)*
	 * 该方法只更新当前缓存，不更新zk。
     * 如果要更新zk请参见 {@link ConfZkClient#setPathDataByKey} 方法
	 * */
	public static void update(String key, String value) {
		if (cache != null) {
			if (StringUtil.isNotEmpty(key)) {
				synchronized(cache) {
					if (cache.get(key)!=null) {
						logger.info(">>>>>>>>>> conf: 更新配置: [{}:{}]", new Object[]{key, value});
						cache.put(new Element(key, value));
					} else {
						logger.info(">>>>>>>>>> conf: 初始化配置: [{}:{}]", new Object[]{key, value});
						cache.put(new Element(key, value));
					}
				}
			}
		}
	}

	/**
	 * 获取当前所有的key
	 * @return
	 */
	public static List<String> getAllKeys(){
		return ConfigHandlerProvider.getInstance().getAllKeys(getAppName());
	}
	
	/**
	* 获取配置信息
	* @param 
	* @return 
	* @Exception 
	* @since 
	*/
	public static String get(String inputAppName, String key, String defaultVal, boolean isWatch) {
		//从缓存获取数据
		if (cache != null) {
			Element element = cache.get(key);
			if (element != null) {
				return (String) element.getObjectValue();
			}
		}
		
		//扩展配置点获取信息
		if (StringUtil.isNotEmpty(inputAppName)) {
			String zkData = ConfigHandlerProvider.getInstance().getDataByKey(inputAppName+Constant.SLASH+key, isWatch);
			if (zkData!=null) {
				update(key, zkData);//更新缓存
				return zkData;
			}
		}
		
		//最后从本地配置文件获取
		try {
			String propertyValue = PropertyUtils.readData(Constant.localProp, key, false);
			if (propertyValue != null) {
				update(key, propertyValue);
				return propertyValue;
			}
		} catch (Exception ex) {
			//nothing
		}
		try {
			String propertyValue = PropertyUtils.readData("config","application", "properties", key, false);
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
	public static String get(String key) {
		return get(key, "", true);
	}
	public static String get(String key, boolean isWatch) {
		return get(key, "", isWatch);
	}
	public static String get(String key, String defaultVal) {
		return get(getAppName(), key, defaultVal, true);
	}
	public static String get(String key, String defaultVal,boolean isWatch) {
		return get(getAppName(), key, defaultVal, isWatch);
	}
	public static String get(String inputAppName, String key, String defaultVal) {
		return 	get(inputAppName, key, defaultVal, true);
	}
	
	/**
	* 删除配置信息(由conf_admin发起的删除)
	* @param 
	* @return 
	* @Exception 
	* @since 
	*/
	public static boolean remove(String key) {
		if (cache != null) {
			if (StringUtil.isNotEmpty(key)) {
				synchronized(cache) {
					if (cache.get(key)!=null) {
						logger.info(">>>>>>>>>> conf: 删除配置：key ", key);
						return cache.remove(key);
					}
				}
			}
		}
		return false;
	}
	
	public static String getAppName() {
		return appName;
	}
	public static void setAppName(String inputAppName) {
		appName = inputAppName;
	}
	
}
