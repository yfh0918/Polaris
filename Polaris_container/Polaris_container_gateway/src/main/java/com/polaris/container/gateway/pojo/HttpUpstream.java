package com.polaris.container.gateway.pojo;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.base.Splitter;
import com.polaris.container.gateway.HttpFilterConstant;
import com.polaris.core.pojo.KeyValuePair;
import com.polaris.core.util.PropertyUtil;

public class HttpUpstream {

    public static final String NAME = "gw_upstream.txt";
    private static volatile Map<String, HttpUpstream> contextMap = new ConcurrentHashMap<>();

    public static void load(Set<String> contents) {
    	Map<String, HttpUpstream> temp_contextMap = new ConcurrentHashMap<>();
        for (String line : contents) {
        	KeyValuePair kv = PropertyUtil.getKVPair(line);
        	HttpUpstream upstream = new HttpUpstream();
        	if (kv != null) {
        		upstream.setContext(kv.getKey());
        		upstream.setServiceName(kv.getValue());
            }
        	temp_contextMap.put(upstream.getContext(), upstream);
        }
        contextMap = temp_contextMap;
    }
    
    public static HttpUpstream getFromContext(String key) {
    	HttpUpstream upstream =  contextMap.get(key);
    	if (upstream == null) {
    		upstream = contextMap.get(HttpFilterConstant.DEFAULT);
    	}
    	return upstream;
    }
    public static Set<Map.Entry<String,HttpUpstream>> getContextEntrySet() {
    	return contextMap.entrySet();
    }
    
    public static HttpUpstream getFromUri(String uri) {
        HttpUpstream upstream = getFromContext(getContextFromUri(uri));
        if (upstream != null) {
        	return upstream;
        }
        return null;
    }
    public static String getContextFromUri(String uri) {
    	List<String> contextList = Splitter.on(HttpFilterConstant.SLASH).omitEmptyStrings().splitToList(uri);
    	return contextList.size() == 0 ? HttpFilterConstant.SLASH : HttpFilterConstant.SLASH + contextList.get(0);
    }

	private String host;//例如:localhost, 127.0.0.1, 0.0.0.0, 192.168.1.1，aiaaifa.com等
    private Integer port;//网关的监听端口号，例如：8081
	private String context;//context，从requst的uri获取，没有为[/]
	private String serviceName;//反向代理的服务名称，可以是IP+port 或者 为注册中心或者DNS的名称
    public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public Integer getPort() {
		return port;
	}
	public void setPort(Integer port) {
		this.port = port;
	}
	
	public String getContext() {
		return context;
	}
	public void setContext(String context) {
		this.context = context;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
}
