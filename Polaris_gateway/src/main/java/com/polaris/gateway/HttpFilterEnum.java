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
    
	//requestFilter
	private static Map<String, Class<?>> filterSwithMap = new HashMap<>();
	private static Map<Class<?>, String> filterMap = new HashMap<>();
	private static Map<Class<?>, Integer> filterOrderMap = new HashMap<>();

	//加入默认过滤器
	static {
		for (HttpFilterEnum e : HttpFilterEnum.values()) {
			filterMap.put(e.getClazz(), e.getSwitch());
			filterOrderMap.put(e.getClazz(), e.getOrder());
			filterSwithMap.put(e.getSwitch(), e.getClazz());
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
	public static void addExtendFilter(String switchKey, Class<?> clazz) {
		Class<?> defautlFilterClass = filterSwithMap.get(switchKey);
		int order = -1;
		if (defautlFilterClass != null) {
			filterSwithMap.remove(switchKey);
			filterMap.remove(defautlFilterClass);
			order = filterOrderMap.get(defautlFilterClass);
			filterOrderMap.remove(defautlFilterClass);
		}
		filterMap.put(clazz, switchKey);
		filterOrderMap.put(clazz, order);
		filterSwithMap.put(switchKey, clazz);
	}
	public static void addExtendFilter( String switchKey, Class<?> clazz, Integer order) {
		Class<?> defautlFilterClass = filterSwithMap.get(switchKey);
		if (defautlFilterClass != null) {
			filterSwithMap.remove(switchKey);
			filterMap.remove(defautlFilterClass);
			filterOrderMap.remove(defautlFilterClass);
		}
		filterMap.put(clazz, switchKey);
		filterOrderMap.put(clazz, order);
		filterSwithMap.put(switchKey, clazz);
	}
}
