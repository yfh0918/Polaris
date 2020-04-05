package com.polaris.core.naming.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.polaris.core.config.ConfClient;
import com.polaris.core.util.WeightedRoundRobinScheduling;
import com.polaris.core.util.WeightedRoundRobinScheduling.Server;

public class ServerHandlerLocalProvider {
    private Map<String, WeightedRoundRobinScheduling> serverMap = new ConcurrentHashMap<>();
    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = null;

    public static ServerHandlerLocalProvider INSTANCE = new ServerHandlerLocalProvider();
    private ServerHandlerLocalProvider() {}
    
    public String getUrl(String key, List<String> serverInfoList) {
    	key = serverInfoList.get(1);
    	
    	String[] serversInfo = key.split(",");
    	if (serversInfo.length == 1) {
    		return serverInfoList.get(0)+ key + serverInfoList.get(2);
    	}
    	
    	//初期化定时器
		if (scheduledThreadPoolExecutor == null) {
			synchronized(ServerHandlerLocalProvider.class){
				if (scheduledThreadPoolExecutor == null) {
					scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
			        scheduledThreadPoolExecutor.scheduleAtFixedRate(new ServerHandlerLocalCheckTask(serverMap), 
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
    	return serverInfoList.get(0)+ server.getIp() + ":" + server.getPort() + serverInfoList.get(2);
    }
    
	protected List<String> getAllUrl(String key,List<String> serverInfoList) {
		return getAllUrl(key,serverInfoList,true);
	}
	
    public List<String> getAllUrl(String key,List<String> serverInfoList, boolean subscribe) {
    	List<String> urlList = new ArrayList<>();
		String[] ips = serverInfoList.get(1).split(",");
		for (int i0 = 0; i0 < ips.length; i0++) {
			urlList.add(serverInfoList.get(0) + ips[i0] + serverInfoList.get(2));
		}
		return urlList;

    }
    
    public boolean connectionFail(String key, String url) {
    	
    	//只有单个url直接返回
    	if (key.split(",").length == 1) {
    		return true;
    	}
    	
    	//多个URL
    	WeightedRoundRobinScheduling weightedRoundRobinScheduling = serverMap.get(key);
    	
    	//只有一个有效服务地址，即使链接失败也不移除
    	if (weightedRoundRobinScheduling.healthilyServers.size() > 1) {
    		String[] si = url.split(":");
            weightedRoundRobinScheduling.unhealthilyServers.add(weightedRoundRobinScheduling.getServer(si[0], Integer.parseInt(si[1])));
            weightedRoundRobinScheduling.healthilyServers.remove(weightedRoundRobinScheduling.getServer(si[0], Integer.parseInt(si[1])));
    	}
    	return true;
    }
    
    public void init() {
    	serverMap.clear();
    }

}
