package com.polaris.container.gateway.pojo;

import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.polaris.container.gateway.GatewayConstant;
import com.polaris.core.util.PropertyUtil;

public class HostUpstream {

    public static final String NAME = "gw_upstream.txt";
    private static volatile Map<String, HostUpstream> contextMap = new ConcurrentHashMap<>();

    public static void load(String[] contents) {
    	Map<String, HostUpstream> temp_contextMap = new ConcurrentHashMap<>();
        for (String line : contents) {
        	line = line.replace("\n", "").replace("\r", "");
        	HostUpstream upstream = HostUpstream.create(line);
        	temp_contextMap.put(upstream.getContext(), upstream);
        }
        contextMap = temp_contextMap;
    }
    private static HostUpstream create(String line) {
    	String[] kv = PropertyUtil.getKeyValue(line);
    	HostUpstream upstram = new HostUpstream();
    	if (kv != null) {
    		upstram.setHost(kv[1]);
    		upstram.setContext(kv[0]);
        }
    	return upstram;
    }
    
    public static HostUpstream getFromContext(String key) {
    	return contextMap.get(key);
    }
    public static Set<Map.Entry<String,HostUpstream>> getContextEntrySet() {
    	return contextMap.entrySet();
    }
    
    public static HostUpstream getFromUri(String uri) {
    	if (!uri.substring(1).contains(GatewayConstant.SLASH)) {
    		uri = uri + GatewayConstant.SLASH;
    	}
        if (uri != null) {
            for (Entry<String, HostUpstream> entry : HostUpstream.getContextEntrySet()) {
                if (uri.startsWith(entry.getKey()+GatewayConstant.SLASH)) {
                    return entry.getValue();
                }
            }
        }
        HostUpstream upstream = HostUpstream.getFromContext(GatewayConstant.DEFAULT);
        if (upstream != null) {
        	return upstream;
        }
        throw new NullPointerException("url is not corrected");
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
