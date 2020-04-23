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
	Cors(new HttpFilterEntity(new CorsRequestFilter(), "gateway.cors", 0)), 
	Degrade(new HttpFilterEntity(new DegradeRequestFilter(), "gateway.degrade", 2)), 
	WIp(new HttpFilterEntity(new WIpHttpRequestFilter(), "gateway.ip.whitelist", 4)), 
	Ip(new HttpFilterEntity(new IpHttpRequestFilter(), "gateway.ip.blacklist", 6)), 
	CC(new HttpFilterEntity(new CCHttpRequestFilter(), "gateway.cc", 8)),
	Scanner(new HttpFilterEntity(new ScannerHttpRequestFilter(), "gateway.scanner", 10)),
	WUrl(new HttpFilterEntity(new WUrlHttpRequestFilter(), "gateway.url.whitelist", 12)),
	Ua(new HttpFilterEntity(new UaHttpRequestFilter(), "gateway.ua", 14)),
	Url(new HttpFilterEntity(new UrlHttpRequestFilter(), "gateway.url.blacklist", 16)),
	Args(new HttpFilterEntity(new ArgsHttpRequestFilter(), "gateway.args", 18)),
	Cookie(new HttpFilterEntity(new CookieHttpRequestFilter(), "gateway.cookie", 20)),
	Post(new HttpFilterEntity(new PostHttpRequestFilter(), "gateway.post", 22)),
	Token(new HttpFilterEntity(new TokenHttpRequestFilter(), "gateway.token", 24)),

	//responseFilter
	CorsResponse(new HttpFilterEntity(new CorsHttpResponseFilter(), "gateway.cors", 0)),
	TokenResponse(new HttpFilterEntity(new TokenHttpResponseFilter(), "gateway.token", 2));
	
	//requestFilter
	private static Map<Class<?>, HttpFilterEntity> filterMap = new HashMap<>();

	//加入默认过滤器
	public static void init() {
		for (HttpFilterEnum e : HttpFilterEnum.values()) {
			e.getFilterEntity().getFilter().init();
			filterMap.put(e.getFilterEntity().getClazz(), e.getFilterEntity());
		}
	}

    // 构造方法  
	private HttpFilterEntity filterEntity;
    private HttpFilterEnum(HttpFilterEntity filterEntity) {  
    	this.filterEntity = filterEntity;
    }
	public HttpFilterEntity getFilterEntity() {
		return this.filterEntity;
	}
	public void setFilterEntity(HttpFilterEntity filterEntity) {
		this.filterEntity = filterEntity;
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

	public synchronized static void replaceFilter(HttpFilterEnum eum, HttpFilter filter) {
		removeFilter(eum.getFilterEntity().getClazz());
		addFilter(filter,eum.getFilterEntity().getKey(),eum.getFilterEntity().getOrder());
	}
	public synchronized static void addFilter(HttpFilter filter, String key, Integer order) {
		filter.init();
		HttpFilterEntity filterEntity = new HttpFilterEntity(filter, key, order);
		filterMap.put(filter.getClass(), filterEntity);
	}
	public synchronized static void removeFilter(Class<?> clazz) {
		filterMap.remove(clazz);
	}
}
