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
	Cors(new HttpFilterObject(CorsRequestFilter.class, "gateway.cors", 0)), 
	Degrade(new HttpFilterObject(DegradeRequestFilter.class, "gateway.degrade", 2)), 
	WIp(new HttpFilterObject(WIpHttpRequestFilter.class, "gateway.ip.whitelist", 4)), 
	Ip(new HttpFilterObject(IpHttpRequestFilter.class, "gateway.ip.blacklist", 6)), 
	CC(new HttpFilterObject(CCHttpRequestFilter.class, "gateway.cc", 8)),
	Scanner(new HttpFilterObject(ScannerHttpRequestFilter.class, "gateway.scanner", 10)),
	WUrl(new HttpFilterObject(WUrlHttpRequestFilter.class, "gateway.url.whitelist", 12)),
	Ua(new HttpFilterObject(UaHttpRequestFilter.class, "gateway.ua", 14)),
	Url(new HttpFilterObject(UrlHttpRequestFilter.class, "gateway.url.blacklist", 16)),
	Args(new HttpFilterObject(ArgsHttpRequestFilter.class, "gateway.args", 18)),
	Cookie(new HttpFilterObject(CookieHttpRequestFilter.class, "gateway.cookie", 20)),
	Post(new HttpFilterObject(PostHttpRequestFilter.class, "gateway.post", 22)),
	Token(new HttpFilterObject(TokenHttpRequestFilter.class, "gateway.token", 24)),

	//responseFilter
	CorsResponse(new HttpFilterObject(CorsHttpResponseFilter.class, "gateway.cors", 0)),
	TokenResponse(new HttpFilterObject(TokenHttpResponseFilter.class, "gateway.token", 2));
	
	//requestFilter
	private static Map<Class<?>, HttpFilterObject> filterMap = new HashMap<>();

	//加入默认过滤器
	static {
		for (HttpFilterEnum e : HttpFilterEnum.values()) {
			filterMap.put(e.getFilterObject().getClazz(), e.getFilterObject());
		}
	}

    // 构造方法  
	private HttpFilterObject filterObj;
    private HttpFilterEnum(HttpFilterObject filterObj) {  
    	this.filterObj = filterObj;
    }
	public HttpFilterObject getFilterObject() {
		return this.filterObj;
	}
	public void setFilterObject(HttpFilterObject filterObj) {
		this.filterObj = filterObj;
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
		filterMap.remove(eum.getFilterObject().getClazz());
		eum.getFilterObject().setClazz(clazz);
		filterMap.put(clazz, eum.getFilterObject());
	}
	public synchronized static void addFilter(Class<? extends HttpFilter> clazz, String key, Integer order) {
		HttpFilterObject filterObj = new HttpFilterObject(clazz, key, order);
		filterMap.put(clazz, filterObj);
	}
	public synchronized static void removeFilter(Class<?> clazz) {
		filterMap.remove(clazz);
	}
}
