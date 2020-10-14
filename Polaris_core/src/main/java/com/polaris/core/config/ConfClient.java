package com.polaris.core.config;

import java.util.Properties;

import com.polaris.core.Constant;
import com.polaris.core.config.Config.Opt;
import com.polaris.core.config.Config.Type;
import com.polaris.core.config.provider.ConfigChangeListenerProxy;
import com.polaris.core.config.provider.ConfHandlerFactory;
import com.polaris.core.util.AppNameUtil;
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
public class ConfClient implements ConfigChangeListener{
    
    private static Properties cache = new Properties();
    
    private static ConfClient INSTANCE = new ConfClient();
    private ConfClient() {}
    
	/**
	* 初始化
	* @param 
	* @return 
	* @Exception 
	* @since 
	*/
	public static void init() {
	    ConfHandlerFactory.create(Type.SYS, INSTANCE, ConfigChangeListenerProxy.INSTANCE);
        ConfHandlerFactory.create(Type.EXT, INSTANCE, ConfigChangeListenerProxy.INSTANCE);
	}
	
	/**
	* 设置配置信息
	* @param 
	* @return 
	* @Exception 
	* @since 
	*/
	public static void set(String key, String value) {
	    cache.put(key, value);
	}
	
	/**
	* 获取配置信息
	* @param 
	* @return 
	* @Exception 
	* @since 
	*/
	public static String get(String key, String... defaultVal) {
	    if (defaultVal == null || defaultVal.length == 0) {
            return cache.getProperty(key);
        }
        return cache.getProperty(key,defaultVal[0]);
	}
	
    /**
    * 获取全体配置
    * @param 
    * @return 
    * @Exception 
    * @since 
    */
    public static Properties get() {
        return cache;
    }
	
	/**
	* 应用名称
	* @param 
	* @return 
	* @Exception 
	* @since 
	*/
	public static String getAppName() {
		return AppNameUtil.getAppName();
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
	public static String getAppGroup() {
	    return getAppGroup(null);
	}
	public static String getAppGroup(String group) {
	    if (StringUtil.isNotEmpty(group)) {
	        return group;
	    }
	    String appGroup = get(Constant.PROJECR_GROUP_NAME);
	    if (StringUtil.isNotEmpty(appGroup)) {
            return appGroup;
        }
        return Constant.DEFAULT_GROUP;
	}

	@Override
    public void onChange(String sequence, String group, String file, Object key, Object value, Opt opt) {
        if (opt != Opt.DEL) {
            cache.put(key, value);
        } else {
            cache.remove(key);
        }
    }
}
