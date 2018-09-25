package com.polaris.core.connect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.polaris.comm.config.ConfClient;
import com.polaris.comm.util.StringUtil;
import com.polaris.comm.util.WeightedRoundRobinScheduling;
import com.polaris.comm.util.WeightedRoundRobinScheduling.Server;

public class ServerDiscovery {
    private static Map<String, WeightedRoundRobinScheduling> serverMap = new ConcurrentHashMap<>();
    private static ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = null;

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
    				String prefix = "";
    				String suffix = "";
    				List<WeightedRoundRobinScheduling.Server> serverList = new ArrayList<>();
    				
    				for (String serverInfo : serversInfo) {
    					List<String> si = getRemoteAddress(serverInfo);
    					if (StringUtil.isNotEmpty(si.get(0))) {
    						prefix = si.get(0);
    					}
    		            if (si.size() == 3) {
    		            	int suffixIndex = si.get(2).indexOf("/");
    		            	int port = -1;
    		                if (suffixIndex > 0) {
    		                	suffix = si.get(2).substring(suffixIndex);
    		                	port = Integer.valueOf(si.get(2).substring(0, suffixIndex));
    		                } else {
    		                	port = Integer.valueOf(si.get(2));
    		                }
     		                WeightedRoundRobinScheduling.Server server = new WeightedRoundRobinScheduling.Server(si.get(1), port, 1);
    		                serverList.add(server);
    		            } else if (si.size() == 4) {
    		            	int suffixIndex = si.get(3).indexOf("/");
    		            	int weight = 1;
    		                if (suffixIndex > 0) {
    		                	suffix = si.get(3).substring(suffixIndex);
    		                	weight = Integer.valueOf(si.get(3).substring(0, suffixIndex));
    		                } else {
    		                	weight = Integer.valueOf(si.get(3));
    		                }
     		                WeightedRoundRobinScheduling.Server server = new WeightedRoundRobinScheduling.Server(si.get(1), Integer.valueOf(si.get(2)), weight);
    		                serverList.add(server);
    		            }
    		        }
    				wrrs = new WeightedRoundRobinScheduling(serverList);
    				wrrs.setPrefix(prefix);
    				wrrs.setSuffix(suffix);
    				serverMap.put(key, wrrs);
    			}
    		}
    	}
    	Server server = wrrs.getServer();
    	return wrrs.getPrefix() + server.getIp() + ":" + server.getPort() + wrrs.getSuffix();
    }
    
    public static void connectionFail(String key, String url) {
    	if (key.split(",").length == 1) {
    		return;
    	}
    	WeightedRoundRobinScheduling weightedRoundRobinScheduling = serverMap.get(key);
    	List<String> si = getRemoteAddress(url);
    	//只有一个有效服务地址，即使链接失败也不移除
    	if (weightedRoundRobinScheduling.healthilyServers.size() > 1) {
    		int suffixIndex = si.get(si.size()-1).indexOf("/");
        	int port = 1;
            if (suffixIndex > 0) {
            	port = Integer.valueOf(si.get(si.size()-1).substring(0, suffixIndex));
            } else {
            	port = Integer.valueOf(si.get(si.size()-1));
            }
            weightedRoundRobinScheduling.unhealthilyServers.add(weightedRoundRobinScheduling.getServer(si.get(1), port));
            weightedRoundRobinScheduling.healthilyServers.remove(weightedRoundRobinScheduling.getServer(si.get(1), port));
    	}
    }
    
    private static List<String> getRemoteAddress(String serverInfo) {
		List<String> serverList = new ArrayList<>(5);
		if (serverInfo.toLowerCase().startsWith(ServerDiscoveryHandlerProvider.HTTP_PREFIX)) {
			serverList.add(ServerDiscoveryHandlerProvider.HTTP_PREFIX);
			serverInfo = serverInfo.substring(ServerDiscoveryHandlerProvider.HTTP_PREFIX.length());
		} else if (serverInfo.toLowerCase().startsWith(ServerDiscoveryHandlerProvider.HTTPS_PREFIX)) {
			serverList.add(ServerDiscoveryHandlerProvider.HTTPS_PREFIX);
			serverInfo = serverInfo.substring(ServerDiscoveryHandlerProvider.HTTPS_PREFIX.length());
		} else {
			serverList.add("");
		}
        serverList.addAll(Arrays.asList(serverInfo.split(":")));
        return serverList;
    }

    public static void main(String[] args) {
    	for (int i0 = 0; i0 < 6; i0++) {
    		String url = getUrl("http://localhost:8080/test/tesaa/afad");
    		System.out.println(url);
    		connectionFail("http://localhost:8080/test/tesaa/afad",url);
    	}
    }

}
