package com.polaris.core.connect;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.polaris.comm.config.ConfClient;
import com.polaris.comm.util.WeightedRoundRobinScheduling;
import com.polaris.comm.util.WeightedRoundRobinScheduling.Server;

public class ServerDiscovery {
    private static Map<String, WeightedRoundRobinScheduling> serverMap = new ConcurrentHashMap<>();
    private static ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = null;
    private static final String HTTP_PREFIX = "http://";
    private static final String HTTPS_PREFIX = "https://";

    public static String getUrl(String key) {
    	String[] serversInfo = key.split(",");
    	if (serversInfo.length == 1) {
    		return key;
    	}
    	
    	//初期化定时器
		if (scheduledThreadPoolExecutor == null) {
			synchronized(ServerDiscovery.class){
				if (scheduledThreadPoolExecutor == null) {
					scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
			        scheduledThreadPoolExecutor.scheduleAtFixedRate(new ServerCheckTask(serverMap), 
			        		Integer.parseInt(ConfClient.get("server.check.cycletime", "60", false)), 
			        		Integer.parseInt(ConfClient.get("server.check.cycletime", "60", false)), TimeUnit.SECONDS);
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
    					String[] si = getRemoteAddress(serverInfo);
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
    	if (key.split(",").length == 1) {
    		return;
    	}
    	WeightedRoundRobinScheduling weightedRoundRobinScheduling = serverMap.get(key);
    	String[] si = getRemoteAddress(url);
    	//只有一个有效服务地址，即使链接失败也不移除
    	if (weightedRoundRobinScheduling.healthilyServers.size() > 1) {
            weightedRoundRobinScheduling.unhealthilyServers.add(weightedRoundRobinScheduling.getServer(si[0], Integer.parseInt(si[1])));
            weightedRoundRobinScheduling.healthilyServers.remove(weightedRoundRobinScheduling.getServer(si[0], Integer.parseInt(si[1])));
    	}
    }
    
    private static String[] getRemoteAddress(String serverInfo) {
		boolean httpPrefix = false;
		boolean httpsPrefix = false;
		if (serverInfo.toLowerCase().startsWith(HTTP_PREFIX)) {
			httpPrefix = true;
			serverInfo = serverInfo.substring(HTTP_PREFIX.length());
		}
		if (serverInfo.toLowerCase().startsWith(HTTPS_PREFIX)) {
			httpsPrefix = true;
			serverInfo = serverInfo.substring(HTTPS_PREFIX.length());
		}
        String[] si = serverInfo.split(":");
    	if (httpPrefix) {
    		si[0] = HTTP_PREFIX + si[0];
    	}
    	if (httpsPrefix) {
    		si[0] = HTTPS_PREFIX + si[0];
    	}
        return si;
    }

    public static void main(String[] args) {
    	getUrl("http://localhost:8080");
    }

}
