package com.polaris.core.config;

import com.polaris.core.config.provider.Config.Opt;

/**
* 配置变化的监听器-用于properties文件，yaml文件
* {@link}ConfigDefault
* {@link}ConfigFactory
*/
public interface ConfigChangeListener {
	
    /**
     * 配置发生变化开始后的回调处理
     * @param  sequence 配置中心以文件为单位，相同的文件sequence也相同
     * @return 
     * @Exception 
     * @since 
     */
    default void onStart(String sequence) {}
     
	/**
	* 配置发生变化的回调处理
	* @param  sequence 配置中心以文件为单位，相同的文件sequence也相同
    * @param  group 
    * @param  file 
	* @param  key 
	* @param  value 
	* @param  opt 
	* @return 
	* @Exception 
	* @since 
	*/
    default void onChange(String sequence, String group, String file, Object key, Object value, Opt opt) {}
	default void onChange(String sequence, Object key, Object value, Opt opt) {}
    default void onChange(Object key, Object value, Opt opt) {}
	
	/**
	* 配置发生变化结束后的回调处理
	* @param  sequence 配置中心以文件为单位，相同的文件sequence也相同
	* @return 
	* @Exception 
	* @since 
	*/
	default void onComplete(String sequence) {}
}
