package com.polaris.container.gateway;

import java.util.HashMap;
import java.util.Map;

import com.polaris.container.gateway.request.ArgsHttpRequestFilter;
import com.polaris.container.gateway.request.CCHttpRequestFilter;
import com.polaris.container.gateway.request.CookieHttpRequestFilter;
import com.polaris.container.gateway.request.CorsRequestFilter;
import com.polaris.container.gateway.request.DegradeRequestFilter;
import com.polaris.container.gateway.request.IpHttpRequestFilter;
import com.polaris.container.gateway.request.PostHttpRequestFilter;
import com.polaris.container.gateway.request.ScannerHttpRequestFilter;
import com.polaris.container.gateway.request.TokenHttpRequestFilter;
import com.polaris.container.gateway.request.UaHttpRequestFilter;
import com.polaris.container.gateway.request.UrlHttpRequestFilter;
import com.polaris.container.gateway.request.WIpHttpRequestFilter;
import com.polaris.container.gateway.request.WUrlHttpRequestFilter;
import com.polaris.container.gateway.response.CorsHttpResponseFilter;
import com.polaris.container.gateway.response.TokenHttpResponseFilter;

public enum HttpFilterEnum {
		
	//默认的requestFilter
	Cors(CorsRequestFilter.class, "gateway.cors", 0), 
	Degrade(DegradeRequestFilter.class, "gateway.degrade", 2), 
	WIp(WIpHttpRequestFilter.class, "gateway.ip.whitelist", 4), 
	Ip(IpHttpRequestFilter.class, "gateway.ip.blacklist", 6), 
	CC(CCHttpRequestFilter.class, "gateway.cc", 8),
	Scanner(ScannerHttpRequestFilter.class, "gateway.scanner", 10),
	WUrl(WUrlHttpRequestFilter.class, "gateway.url.whitelist", 12),
	Ua(UaHttpRequestFilter.class, "gateway.ua", 14),
	Url(UrlHttpRequestFilter.class, "gateway.url.blacklist", 16),
	Args(ArgsHttpRequestFilter.class, "gateway.args", 18),
	Cookie(CookieHttpRequestFilter.class, "gateway.cookie", 20),
	Post(PostHttpRequestFilter.class, "gateway.post", 22),
	Token(TokenHttpRequestFilter.class, "gateway.token", 24),

	//responseFilter
	CorsResponse(CorsHttpResponseFilter.class, "gateway.cors", 0),
	TokenResponse(TokenHttpResponseFilter.class, "gateway.token", 2);
	
	// 成员变量  
    private int order;  
    private String key;
    private Class<? extends HttpFilter> clazz;
    
	//requestFilter
	private static Map<Class<?>, HttpFilterEnum> filterMap = new HashMap<>();

	//加入默认过滤器
	static {
		for (HttpFilterEnum e : HttpFilterEnum.values()) {
			filterMap.put(e.getClazz(), e);
		}
	}

    // 构造方法  
    private HttpFilterEnum(Class<? extends HttpFilter> clazz, String key, int order) {  
    	this.clazz = clazz;
    	this.key = key;
        this.order = order;   
    }
	public Class<? extends HttpFilter> getClazz() {
		return this.clazz;
	}
	public void setClazz(Class<? extends HttpFilter> clazz) {
		this.clazz = clazz;
	}
	public String getKey() {
		return this.key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public void setOrder(Integer order) {
		this.order = order;
	}
	public Integer getOrder() {
		return order;
	}
	
	public static String getKey(Class<?> clazz) {
		if (filterMap.containsKey(clazz)) {
			return filterMap.get(clazz).getKey();
		}
		return null;
	}
	

	public static Integer getOrder(Class<?> clazz) {
		if (filterMap.containsKey(clazz)) {
			return filterMap.get(clazz).getOrder();
		}
		
		return -1;
	}

	public synchronized static void replaceFilter(HttpFilterEnum eum, Class<? extends HttpFilter> clazz) {
		filterMap.remove(eum.getClazz());
		eum.setClazz(clazz);
		filterMap.put(clazz, eum);
	}
	public synchronized static void removeFilter(Class<?> clazz) {
		filterMap.remove(clazz);
	}
}
