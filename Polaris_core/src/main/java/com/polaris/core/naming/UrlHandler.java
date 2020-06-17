package com.polaris.core.naming;

import java.util.List;

public interface UrlHandler {
	
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
