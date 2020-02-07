package com.polaris.container.gateway;

import java.util.HashMap;
import java.util.Map;

import com.polaris.container.gateway.request.ArgsHttpRequestFilter;
import com.polaris.container.gateway.request.CCHttpRequestFilter;
import com.polaris.container.gateway.request.CookieHttpRequestFilter;
import com.polaris.container.gateway.request.IpHttpRequestFilter;
import com.polaris.container.gateway.request.PostHttpRequestFilter;
import com.polaris.container.gateway.request.ScannerHttpRequestFilter;
import com.polaris.container.gateway.request.TokenHttpRequestFilter;
import com.polaris.container.gateway.request.UaHttpRequestFilter;
import com.polaris.container.gateway.request.UrlHttpRequestFilter;
import com.polaris.container.gateway.request.WIpHttpRequestFilter;
import com.polaris.container.gateway.request.WUrlHttpRequestFilter;
import com.polaris.container.gateway.response.ClickjackHttpResponseFilter;
import com.polaris.container.gateway.response.TokenHttpResponseFilter;
import com.polaris.core.util.StringUtil;

public enum HttpFilterEnum {
		
	//默认的requestFilter
	WIp(WIpHttpRequestFilter.class, "gateway.ip.whitelist", 1), 
	Ip(IpHttpRequestFilter.class, "gateway.ip.blacklist", 2), 
	CC(CCHttpRequestFilter.class, "gateway.cc", 3),
	Scanner(ScannerHttpRequestFilter.class, "gateway.scanner", 4),
	WUrl(WUrlHttpRequestFilter.class, "gateway.url.whitelist", 5),
	Ua(UaHttpRequestFilter.class, "gateway.ua", 6),
	Url(UrlHttpRequestFilter.class, "gateway.url.blacklist", 7),
	Args(ArgsHttpRequestFilter.class, "gateway.args", 8),
	Cookie(CookieHttpRequestFilter.class, "gateway.cookie", 9),
	Post(PostHttpRequestFilter.class, "gateway.post", 10),
	Token(TokenHttpRequestFilter.class, "gateway.token", 11),

	//responseFilter
	ResponseClickjack(ClickjackHttpResponseFilter.class, "gateway.response.clickjack", 1),
	ResponseToken(TokenHttpResponseFilter.class, "gateway.response.token", 2);
	
	// 成员变量  
    private int order;  
    private String key;
    private Class<?> clazz;
    
	//requestFilter
	private static Map<String, Class<?>> filterKeyMap = new HashMap<>();
	private static Map<Class<?>, String> filterMap = new HashMap<>();
	private static Map<Class<?>, Integer> filterOrderMap = new HashMap<>();

	//加入默认过滤器
	static {
		for (HttpFilterEnum e : HttpFilterEnum.values()) {
			filterMap.put(e.getClazz(), e.getKey());
			filterOrderMap.put(e.getClazz(), e.getOrder());
			filterKeyMap.put(e.getKey(), e.getClazz());
		}
	}

    // 构造方法  
    private HttpFilterEnum(Class<?> clazz, String key, int order) {  
    	this.clazz = clazz;
    	this.key = key;
        this.order = order;   
    }
	public Class<?> getClazz() {
		return clazz;
	}
	
	//获取开关
	public String getKey() {
		return key;
	}
	public static String getKey(Class<?> clazz) {
		
		//循环扩展过滤器
		String strSwitch = filterMap.get(clazz);
		if (StringUtil.isNotEmpty(strSwitch)) {
			return strSwitch;
		}
		return null;
	}
	
	//获取排序
	public Integer getOrder() {
		return order;
	}
	public static Integer getOrder(Class<?> clazz) {
		//有限扩展
		Integer intOrder = filterOrderMap.get(clazz);
		if (intOrder != null) {
			return intOrder;
		}
		return -1;
	}
	public synchronized static void addExtendFilter(String switchKey, Class<?> clazz) {
		Class<?> defautlFilterClass = filterKeyMap.get(switchKey);
		int order = -1;
		if (defautlFilterClass != null) {
			filterKeyMap.remove(switchKey);
			filterMap.remove(defautlFilterClass);
			order = filterOrderMap.get(defautlFilterClass);
			filterOrderMap.remove(defautlFilterClass);
		}
		filterMap.put(clazz, switchKey);
		filterOrderMap.put(clazz, order);
		filterKeyMap.put(switchKey, clazz);
	}
	public synchronized static void addExtendFilter( String switchKey, Class<?> clazz, Integer order) {
		Class<?> defautlFilterClass = filterKeyMap.get(switchKey);
		if (defautlFilterClass != null) {
			filterKeyMap.remove(switchKey);
			filterMap.remove(defautlFilterClass);
			filterOrderMap.remove(defautlFilterClass);
		}
		filterMap.put(clazz, switchKey);
		filterOrderMap.put(clazz, order);
		filterKeyMap.put(switchKey, clazz);
	}
}
