package com.polaris.core.naming.provider;

import java.util.List;

import com.polaris.core.naming.ServerHandlerLocal;

public interface ServerStrategyProvider extends ServerHandlerLocal{
	void connectionFail(String serviceNameUrl, String realIpUrl);
	String getRealIpUrl(String serviceNameUrl);
	List<String> getRealIpUrlList(String serviceNameUrl);
}
