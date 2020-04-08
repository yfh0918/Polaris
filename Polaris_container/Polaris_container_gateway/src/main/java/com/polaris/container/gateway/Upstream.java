package com.polaris.container.gateway;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.polaris.core.util.PropertyUtil;

public class Upstream {

    public static final String NAME = "upstream.txt";

    private static volatile Map<String, Upstream> contextMap = new ConcurrentHashMap<>();
    private static volatile Map<String, Upstream> virtualPortMap = new ConcurrentHashMap<>();

    public static void load(String[] contents) {
    	Map<String, Upstream> temp_contextMap = new ConcurrentHashMap<>();
        Map<String, Upstream> temp_virtualPortMap = new ConcurrentHashMap<>();
    	int port = 7000;
        for (String line : contents) {
        	line = line.replace("\n", "").replace("\r", "");
        	Upstream upstream = Upstream.create(line, String.valueOf(port));
        	temp_contextMap.put(upstream.getContext(), upstream);
        	temp_virtualPortMap.put(upstream.getVirtualPort(), upstream);
            port++;
        }
        contextMap = temp_contextMap;
        virtualPortMap = temp_virtualPortMap;
    }
    private static Upstream create(String line, String virtualPort) {
    	String[] kv = PropertyUtil.getKeyValue(line);
    	Upstream upstram = new Upstream();
    	if (kv != null) {
    		upstram.setHost(kv[1]);
    		upstram.setContext(kv[0]);
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
    public static Upstream getFromVirtualPort(String key) {
    	return virtualPortMap.get(key);
    }
    public static Set<Map.Entry<String,Upstream>> getVirtualPortEntrySet() {
    	return virtualPortMap.entrySet();
    }
 
	private String context;
	private String virtualPort;
	private String host;
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
}
