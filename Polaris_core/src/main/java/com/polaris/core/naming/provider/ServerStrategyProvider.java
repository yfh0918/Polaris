package com.polaris.core.naming.provider;

import java.util.List;

import com.polaris.core.naming.ServerHandler;

public interface ServerStrategyProvider extends ServerHandler{
	
	/**
	* 获取真实的IP+url
	* @param 
	* @return 
	* @Exception 
	* @since 
	*/
	String getRealIpUrl(String serviceNameUrl);
	
	/**
	* 获取真实的IP+url 列表
	* @param 
	* @return 
	* @Exception 
	* @since 
	*/
	List<String> getRealIpUrlList(String serviceNameUrl);
}
