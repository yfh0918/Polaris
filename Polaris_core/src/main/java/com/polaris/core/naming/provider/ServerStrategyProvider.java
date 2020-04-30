package com.polaris.core.naming.provider;

import java.util.List;

import com.polaris.core.naming.ServerHandlerLocal;

public interface ServerStrategyProvider extends ServerHandlerLocal{
	
	/**
	* 连接失败需要特殊处理
	* @param 
	* @return 
	* @Exception 
	* @since 
	*/
	void connectionFail(String serviceNameUrl, String realIpUrl);
	
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
