package com.polaris.core.naming.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.polaris.core.config.ConfClient;
import com.polaris.core.naming.ServerHandlerLocal;
import com.polaris.core.pojo.Server;
import com.polaris.core.util.WeightedRoundRobinScheduling;

public class ServerHandlerLocalProvider implements ServerHandlerLocal{
    private Map<String, WeightedRoundRobinScheduling> serverMap = new ConcurrentHashMap<>();
    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = null;

    public static ServerHandlerLocalProvider INSTANCE = new ServerHandlerLocalProvider();
    private ServerHandlerLocalProvider() {}
    
    @Override
    public Server getServer(String serviceName) {
    	String[] serversInfo = serviceName.split(",");
    	if (serversInfo.length == 1) {
    		String[] si = serversInfo[0].split(":");
            if (si.length == 2) {
                Server server = new Server(si[0], Integer.valueOf(si[1]), 1);
                return server;
            } else if (si.length == 3) {
	            Server server = new Server(si[0], Integer.valueOf(si[1]), Integer.valueOf(si[2]));
	            return server;
            }
    		return null;
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

    	WeightedRoundRobinScheduling wrrs = serverMap.get(serviceName);
    	if (wrrs == null) {
    		synchronized(serviceName.intern()){
    			wrrs = serverMap.get(serviceName);
    			if (wrrs == null) {
    				List<Server> serverList = new ArrayList<>();
    				for (String serverInfo : serversInfo) {
    					String[] si = serverInfo.split(":");
    					
    					// ip:port
    		            if (si.length == 2) {
     		                Server server = new Server(si[0], Integer.valueOf(si[1]), 1);
    		                serverList.add(server);
    		            } else if (si.length == 3) {
     		                Server server = new Server(si[0], Integer.valueOf(si[1]), Integer.valueOf(si[2]));
    		                serverList.add(server);
    		            }
    		        }
    				wrrs = new WeightedRoundRobinScheduling(serverList);
    				serverMap.put(serviceName, wrrs);
    			}
    		}
    	}
    	Server server = wrrs.getServer();
    	return server;
    }
    
    @Override
    public List<Server> getServerList(String seviceName) {
		String[] ips = seviceName.split(",");
		List<Server> serverList = new ArrayList<>();
		for (int i0 = 0; i0 < ips.length; i0++) {
			String[] si = ips[i0].split(":");
            if (si.length == 2) {
                Server server = new Server(si[0], Integer.valueOf(si[1]), 1);
                serverList.add(server);
            } else if (si.length == 3) {
	            Server server = new Server(si[0], Integer.valueOf(si[1]), Integer.valueOf(si[2]));
	            serverList.add(server);
            }
		}
		return serverList;

    }
    
    @Override
    public void connectionFail(String serviceName, Server server) {
    	
    	//只有单个url直接返回
    	if (serviceName.split(",").length == 1) {
    		return;
    	}
    	
    	//多个URL
    	WeightedRoundRobinScheduling weightedRoundRobinScheduling = serverMap.get(serviceName);
    	if (weightedRoundRobinScheduling.healthilyServers.size() > 1) {
            weightedRoundRobinScheduling.unhealthilyServers.add(weightedRoundRobinScheduling.getServer(server.getIp(), server.getPort()));
            weightedRoundRobinScheduling.healthilyServers.remove(weightedRoundRobinScheduling.getServer(server.getIp(), server.getPort()));
    	}
    	return;
    }
    
    @Override
    public void init() {
    	serverMap.clear();
    }
}
