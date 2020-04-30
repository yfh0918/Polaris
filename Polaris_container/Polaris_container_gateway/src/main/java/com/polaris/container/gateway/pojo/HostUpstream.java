package com.polaris.container.gateway.pojo;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.base.Splitter;
import com.polaris.container.gateway.HttpFilterConstant;
import com.polaris.core.pojo.KeyValuePair;
import com.polaris.core.util.PropertyUtil;

public class HostUpstream {

    public static final String NAME = "gw_upstream.txt";
    private static volatile Map<String, HostUpstream> contextMap = new ConcurrentHashMap<>();

    public static void create(Set<String> contents) {
    	Map<String, HostUpstream> temp_contextMap = new ConcurrentHashMap<>();
        for (String line : contents) {
        	KeyValuePair kv = PropertyUtil.getKeyValue(line);
        	HostUpstream upstream = new HostUpstream();
        	if (kv != null) {
        		upstream.setContext(kv.getKey());
        		upstream.setHost(kv.getValue());
            }
        	temp_contextMap.put(upstream.getContext(), upstream);
        }
        contextMap = temp_contextMap;
    }
    
    public static HostUpstream getFromContext(String key) {
    	return contextMap.get(key);
    }
    public static Set<Map.Entry<String,HostUpstream>> getContextEntrySet() {
    	return contextMap.entrySet();
    }
    
    public static HostUpstream getFromUri(String uri) {
        HostUpstream upstream = getFromContext(getContextFromUri(uri));
        if (upstream != null) {
        	return upstream;
        }
        upstream = getFromContext(HttpFilterConstant.DEFAULT);
        if (upstream != null) {
        	return upstream;
        }
        return null;
    }
    public static String getContextFromUri(String uri) {
    	List<String> contextList = Splitter.on(HttpFilterConstant.SLASH).omitEmptyStrings().splitToList(uri);
    	return contextList.size() == 0 ? HttpFilterConstant.SLASH : HttpFilterConstant.SLASH + contextList.get(0);
    }

	private String context;
	private String host;
	public String getContext() {
		return context;
	}
	public void setContext(String context) {
		this.context = context;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
}
