package com.polaris.core.naming;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.polaris.core.config.ConfClient;
import com.polaris.core.util.WeightedRoundRobinScheduling;
import com.polaris.core.util.WeightedRoundRobinScheduling.Server;

public class ServerHandlerSupport {
	public static final String HTTP_PREFIX = "http://";
	public static final String HTTPS_PREFIX = "https://";

    private static Map<String, WeightedRoundRobinScheduling> serverMap = new ConcurrentHashMap<>();
    private static ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = null;

    public static String getUrl(String key) {
    	String[] serversInfo = key.split(",");
    	if (serversInfo.length == 1) {
    		return key;
    	}
    	
    	//初期化定时器
		if (scheduledThreadPoolExecutor == null) {
			synchronized(ServerHandlerSupport.class){
				if (scheduledThreadPoolExecutor == null) {
					scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
			        scheduledThreadPoolExecutor.scheduleAtFixedRate(new ServerCheckTask(serverMap), 
			        		Integer.parseInt(ConfClient.get("server.check.cycletime", "30")), 
			        		Integer.parseInt(ConfClient.get("server.check.cycletime", "30")), TimeUnit.SECONDS);
				}
			}
		}

    	WeightedRoundRobinScheduling wrrs = serverMap.get(key);
    	if (wrrs == null) {
    		synchronized(key.intern()){
    			wrrs = serverMap.get(key);
    			if (wrrs == null) {
    				List<WeightedRoundRobinScheduling.Server> serverList = new ArrayList<>();
    				for (String serverInfo : serversInfo) {
    					String[] si = serverInfo.split(":");
    					
    					// ip:port
    		            if (si.length == 2) {
     		                WeightedRoundRobinScheduling.Server server = new WeightedRoundRobinScheduling.Server(si[0], Integer.valueOf(si[1]), 1);
    		                serverList.add(server);
    		            } else if (si.length == 3) {
     		                WeightedRoundRobinScheduling.Server server = new WeightedRoundRobinScheduling.Server(si[0], Integer.valueOf(si[1]), Integer.valueOf(si[2]));
    		                serverList.add(server);
    		            }
    		        }
    				wrrs = new WeightedRoundRobinScheduling(serverList);
    				serverMap.put(key, wrrs);
    			}
    		}
    	}
    	Server server = wrrs.getServer();
    	return server.getIp() + ":" + server.getPort();
    }
    
    public static void connectionFail(String key, String url) {
    	
    	//只有单个url直接返回
    	if (key.split(",").length == 1) {
    		return;
    	}
    	
    	//多个URL
    	WeightedRoundRobinScheduling weightedRoundRobinScheduling = serverMap.get(key);
    	
    	//只有一个有效服务地址，即使链接失败也不移除
    	if (weightedRoundRobinScheduling.healthilyServers.size() > 1) {
    		String[] si = url.split(":");
            weightedRoundRobinScheduling.unhealthilyServers.add(weightedRoundRobinScheduling.getServer(si[0], Integer.parseInt(si[1])));
            weightedRoundRobinScheduling.healthilyServers.remove(weightedRoundRobinScheduling.getServer(si[0], Integer.parseInt(si[1])));
    	}
    }
    

	public static List<String> getRemoteAddress(String serverInfo) {
		List<String> serverList = new ArrayList<>(3);
		if (serverInfo.toLowerCase().startsWith(HTTP_PREFIX)) {
			serverList.add(HTTP_PREFIX);
			serverInfo = serverInfo.substring(HTTP_PREFIX.length());
		} else if (serverInfo.toLowerCase().startsWith(HTTPS_PREFIX)) {
			serverList.add(HTTPS_PREFIX);
			serverInfo = serverInfo.substring(HTTPS_PREFIX.length());
		} else {
			serverList.add("");
		}
		int suffixIndex = serverInfo.indexOf("/");
		if (suffixIndex > 0) {
			serverList.add(serverInfo.substring(0, suffixIndex));
			serverList.add(serverInfo.substring(suffixIndex));
		} else {
			serverList.add(serverInfo);
			serverList.add("");
		}
        return serverList;
    }
	public static boolean isSkip(String key) {
		if (key.toLowerCase().startsWith("www.")) {
			return true;
		}
		if (key.toLowerCase().endsWith(".com") || key.toLowerCase().endsWith(".cn")) {
			return true;
		}
		if (key.contains(",") || key.contains(":")) {
			return true;
		}
		return false;
	}
	
    public static void main(String[] args) {
    	for (int i0 = 0; i0 < 6; i0++) {
    		String url = getUrl("localhost:8080,localhost:8081");
    		System.out.println(url);
    		connectionFail("localhost:8080,localhost:8081",url);
    	}
    }
    
    //如果重新配置了URL原先的serverMap全部清空重来
    public static void reset() {
    	serverMap.clear();
    }

}
