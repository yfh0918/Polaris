package com.polaris.core.naming;

import java.util.List;

import com.polaris.core.pojo.Server;

/**
*
* 项目名称：Polaris
* 类名称：ServerHandler
* 类描述：注册中心扩展
* 创建人：yufenghua
* 创建时间：2018年5月9日 上午9:15:12
* 修改备注：
* @version
*
*/
public interface ServerHandler {
	
	/**
	* 注册到注册中心
	* @param 
	* @return 
	* @Exception 
	* @since 
	*/
	boolean register(String ip, int port);
	
	/**
	* 从注册中心删除
	* @param 
	* @return 
	* @Exception 
	* @since 
	*/
	boolean deregister(String ip, int port);
	
	/**
	* 根据注册的服务名称获取真实的IP，由注册中心实现实现了负载均衡
	* @param 
	* @return 
	* @Exception 
	* @since 
	*/
	default Server getServer(String serviceName) {return null;}
	
	/**
	* 根据注册的服务名称获取真实的IP列表
	* @param 
	* @return 
	* @Exception 
	* @since 
	*/
	default List<Server> getServerList(String serviceName) {return null;}

}
