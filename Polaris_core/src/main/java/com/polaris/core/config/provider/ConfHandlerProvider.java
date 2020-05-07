package com.polaris.core.config.provider;

import com.polaris.core.config.ConfHandlerListener;
import com.polaris.core.config.ConfigListener;

public interface ConfHandlerProvider {
	
	/**
	* 配置中心-初始化
	* @param  configListener
	* @return 
	* @Exception 
	* @since 
	*/
	default void init(ConfigListener configListener) {}
	
	/**
	* 配置中心-获取配置并且监听 = get + listen
	* @param  file 
	* @return 
	* @Exception 
	* @since 
	*/
    default boolean getAndListen(String file) {return true;}
    
	/**
	* 配置中心-获取配置
	* @param  file 
	* @return 
	* @Exception 
	* @since 
	*/
    default String get(String file) {return null;}
    
	/**
	* 配置中心-监听配置变化
	* @param  file 
	* @return 
	* @Exception 
	* @since 
	*/
    default void listen(String file, ConfHandlerListener listener) {}
}
