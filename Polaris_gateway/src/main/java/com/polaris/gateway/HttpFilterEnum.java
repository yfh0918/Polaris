package com.polaris.gateway;

import java.util.HashMap;
import java.util.Map;

import com.polaris.core.util.StringUtil;
import com.polaris.gateway.request.ArgsHttpRequestFilter;
import com.polaris.gateway.request.CCHttpRequestFilter;
import com.polaris.gateway.request.CookieHttpRequestFilter;
import com.polaris.gateway.request.IpHttpRequestFilter;
import com.polaris.gateway.request.PostHttpRequestFilter;
import com.polaris.gateway.request.ScannerHttpRequestFilter;
import com.polaris.gateway.request.TokenHttpRequestFilter;
import com.polaris.gateway.request.UaHttpRequestFilter;
import com.polaris.gateway.request.UrlHttpRequestFilter;
import com.polaris.gateway.request.WIpHttpRequestFilter;
import com.polaris.gateway.request.WUrlHttpRequestFilter;
import com.polaris.gateway.response.ClickjackHttpResponseFilter;

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
	Clickjack(ClickjackHttpResponseFilter.class, "gateway.click.jack", 1);
	
	// 成员变量  
    private int order;  
    private String switc;
    private Class<?> clazz;
    
	//扩展的requestFilter
	private static Map<String, Integer> defaultFilterSwith = new HashMap<>();
	private static Map<Class<?>, String> defaultFilter = new HashMap<>();
	private static Map<Class<?>, Integer> defaultFilterOrder = new HashMap<>();

	//扩展的requestFilter
	private static Map<String, Integer> extendFilterSwith = new HashMap<>();
	private static Map<Class<?>, String> extendFilter = new HashMap<>();
	private static Map<Class<?>, Integer> extendFilterOrder = new HashMap<>();

	//加入默认过滤器
	static {
		for (HttpFilterEnum e : HttpFilterEnum.values()) {
			defaultFilter.put(e.getClazz(), e.getSwitch());
			defaultFilterOrder.put(e.getClazz(), e.getOrder());
			defaultFilterSwith.put(e.getSwitch(), e.getOrder());
		}
	}

    // 构造方法  
    private HttpFilterEnum(Class<?> clazz, String switc, int order) {  
    	this.clazz = clazz;
    	this.switc = switc;
        this.order = order;   
    }
	public Class<?> getClazz() {
		return clazz;
	}
	
	//获取开关
	public String getSwitch() {
		return switc;
	}
	public static String getSwitch(Class<?> clazz) {
		
		//循环扩展过滤器
		String strSwitch = extendFilter.get(clazz);
		if (StringUtil.isNotEmpty(strSwitch)) {
			return strSwitch;
		}
		
		//循环默认的过滤器
		strSwitch = defaultFilter.get(clazz);
		if (StringUtil.isNotEmpty(strSwitch)) {
			if (extendFilterSwith.containsKey(strSwitch)) {
				return null;
			}
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
		Integer intOrder = extendFilterOrder.get(clazz);
		if (intOrder != null) {
			return intOrder;
		}
		
		//默认
		intOrder = defaultFilterOrder.get(clazz);
		if (intOrder != null) {
			return intOrder;
		}
		return -1;
	}
	public static void addExtendFilter(String switchKey, Class<?> clazz) {
		extendFilter.put(clazz, switchKey);
		Integer intOrder = defaultFilterSwith.get(switchKey);
		extendFilterOrder.put(clazz, intOrder);
		extendFilterSwith.put(switchKey, intOrder);
	}
	public static void addExtendFilter( String switchKey, Class<?> clazz, Integer order) {
		extendFilter.put(clazz, switchKey);
		extendFilterOrder.put(clazz, order);
		extendFilterSwith.put(switchKey, order);
	}
}
