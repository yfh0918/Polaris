package com.polaris.core.config;

import com.polaris.core.config.Config.Opt;

/**
* 配置变化的监听器
* {@link}ConfCompositeProvider
* {@link}ConfHandlerStrategyFactory
* {@link}ConfHandlerStrategyDefault
*/
public interface ConfigListener {
	
	/**
	* 配置发生变化的回调处理
	* @param  sequence 配置中心以文件为单位，相同的文件sequence也相同
	* @param  key 
	* @param  value 
	* @param  opt 
	* @return 
	* @Exception 
	* @since 
	*/
	default void onChange(String sequence, Object key, Object value, Opt opt) {}
	
	/**
	* 配置发生变化的回调处理
	* @param  sequence 配置中心以文件为单位，相同的文件sequence也相同
	* @return 
	* @Exception 
	* @since 
	*/
	default void onComplete(String sequence) {}
}
