package com.polaris.gateway;

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
	
	//requestFilter
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
		for (HttpFilterEnum e : HttpFilterEnum.values()) {  
		    if (e.getClazz() == clazz) {
		    	return e.getSwitch();
		    }
		}
		return null;
	}
	
	//获取排序
	public Integer getOrder() {
		return order;
	}
	public static Integer getOrder(Class<?> clazz) {
		for (HttpFilterEnum e : HttpFilterEnum.values()) {  
		    if (e.getClazz() == clazz) {
		    	return e.getOrder();
		    }
		}
		return -1;
	}
}
