package com.polaris.container.gateway;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.polaris.core.util.PropertyUtil;

public class Upstream {

    public static final String NAME = "upstream.txt";
    public static final String STATIC_PREFIX = "static:";

    private static volatile Map<String, Upstream> contextMap = new ConcurrentHashMap<>();
    private static volatile Map<String, Upstream> staticMap = new ConcurrentHashMap<>();
    private static volatile Map<String, Upstream> virtualPortMap = new ConcurrentHashMap<>();

    public static void load(String[] contents) {
    	Map<String, Upstream> temp_contextMap = new ConcurrentHashMap<>();
    	Map<String, Upstream> temp_staticMap = new ConcurrentHashMap<>();
        Map<String, Upstream> temp_virtualPortMap = new ConcurrentHashMap<>();
    	int port = 7000;
        for (String line : contents) {
        	line = line.replace("\n", "").replace("\r", "");
        	Upstream upstream = Upstream.load(line, String.valueOf(port));
        	temp_contextMap.put(upstream.getContext(), upstream);
        	if (upstream.isStatic()) {
        		temp_staticMap.put(upstream.getContext(), upstream);
        	}
        	temp_virtualPortMap.put(upstream.getVirtualPort(), upstream);
            port++;
        }
        contextMap = temp_contextMap;
        staticMap = temp_staticMap;
        virtualPortMap = temp_virtualPortMap;
    }
    private static Upstream load(String line, String virtualPort) {
    	String[] kv = PropertyUtil.getKeyValue(line);
    	Upstream upstram = new Upstream();
    	if (kv != null) {
    		upstram.setHost(kv[1]);
            if (kv[0].startsWith(STATIC_PREFIX)) {
            	String key = kv[0].substring(STATIC_PREFIX.length());
            	upstram.setStatic(true);
        		upstram.setContext(key);
            } else {
        		upstram.setContext(kv[0]);
            }
            upstram.setVirtualPort(virtualPort);
        }
    	return upstram;
    }
    
    public static Upstream getFromContext(String key) {
    	return contextMap.get(key);
    }
    public static Set<Map.Entry<String,Upstream>> getContextEntrySet() {
    	return contextMap.entrySet();
    }
    public static Upstream getFromStatic(String key) {
    	return staticMap.get(key);
    }
    public static Set<String> getStaticSet() {
    	return staticMap.keySet();
    }
    public static Upstream getFromvirtualPort(String key) {
    	return virtualPortMap.get(key);
    }
    
 
	private String context;
	private String virtualPort;
	private String host;
	private boolean isStatic = false;
	public String getContext() {
		return context;
	}
	public void setContext(String context) {
		this.context = context;
	}
	public String getVirtualPort() {
		return virtualPort;
	}
	public void setVirtualPort(String virtualPort) {
		this.virtualPort = virtualPort;
	}


	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public boolean isStatic() {
		return isStatic;
	}
	public void setStatic(boolean isStatic) {
		this.isStatic = isStatic;
	}
}
