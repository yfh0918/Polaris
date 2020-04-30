package com.polaris.core.naming;

import com.polaris.core.pojo.Server;

/**
*
* 项目名称：Polaris
* 类名称：ServerHandlerLocal
* 类描述：非注册中心
* 创建人：yufenghua
* 创建时间：2018年5月9日 上午9:15:12
* 修改备注：
* @version
*
*/
public interface ServerHandlerLocal extends ServerHandler {
	
	/**
	* 本地需要初始化操作
	* @param 
	* @return 
	* @Exception 
	* @since 
	*/
	void init();
	
	/**
	* 本地无注册中心默认返回true
	* @param 
	* @return 
	* @Exception 
	* @since 
	*/
	default boolean register(String ip, int port) {
		return true;
	}

	/**
	* 本地无注册中心默认返回true
	* @param 
	* @return 
	* @Exception 
	* @since 
	*/
	default boolean deregister(String ip, int port) {
		return true;
	}
	
	/**
	* 本地连接失败需要特殊处理
	* @param 
	* @return 
	* @Exception 
	* @since 
	*/
	default void connectionFail(String serviceName, Server server) {}
}
