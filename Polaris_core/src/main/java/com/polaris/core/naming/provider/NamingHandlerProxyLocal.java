package com.polaris.core.naming.provider;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Splitter;
import com.polaris.core.config.ConfClient;
import com.polaris.core.naming.NamingHandler;
import com.polaris.core.pojo.Server;
import com.polaris.core.util.WeightedRoundRobinScheduling;

public class NamingHandlerProxyLocal implements NamingHandler{
    private Map<String, WeightedRoundRobinScheduling> serviceNameMap = new ConcurrentHashMap<>();
    private Map<Server, String> serverMap = new ConcurrentHashMap<>();
    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = null;

    public static NamingHandlerProxyLocal INSTANCE = new NamingHandlerProxyLocal();
    private NamingHandlerProxyLocal() {}
    
    @Override
    public Server getServer(String serviceName) {
    	List<String> serversInfo = Splitter.on(",").omitEmptyStrings().splitToList(serviceName);
    	if (serversInfo.size() == 0) {
    		return null;
    	} else if (serversInfo.size() == 1) {
    		Server server = Server.of(serversInfo.get(0));
    		
    		//删除old
    		String oldServiceName = serverMap.get(server);
    		if (oldServiceName != null) {
    			Set<String> oldServiceNameSet = new HashSet<>();
    			oldServiceNameSet.add(oldServiceName);
    			removeOldServer(oldServiceNameSet);
    		}
    		return server;
    	}
    	
    	//初期化定时器
		if (scheduledThreadPoolExecutor == null) {
			synchronized(NamingHandlerProxyLocal.class){
				if (scheduledThreadPoolExecutor == null) {
					scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
			        scheduledThreadPoolExecutor.scheduleAtFixedRate(new UnhealthyCheckTask(serviceNameMap), 
			        		Integer.parseInt(ConfClient.get("server.check.cycletime", "30")), 
			        		Integer.parseInt(ConfClient.get("server.check.cycletime", "30")), TimeUnit.SECONDS);
				}
			}
		}
    	WeightedRoundRobinScheduling wrrs = serviceNameMap.get(serviceName);
    	if (wrrs == null) {
    		synchronized(serviceName.intern()){
    			wrrs = serviceNameMap.get(serviceName);
    			if (wrrs == null) {
    				List<Server> serverList = new ArrayList<>();
    				for (String serverInfo : serversInfo) {
    					Server server = Server.of(serverInfo);
    					if (server != null) {
    						serverList.add(server);
    					}
    		        }
    				
    				//删除old
    				Set<String> oldServiceNameSet = new HashSet<>();
    				for (Server server : serverList) {
    					String oldServiceName = serverMap.get(server);
    					if (oldServiceName != null) {
    						oldServiceNameSet.add(oldServiceName);
    					}
    				}
    		    	removeOldServer(oldServiceNameSet);
    				
    				//新增new
    				wrrs = new WeightedRoundRobinScheduling(serverList);
    				serviceNameMap.put(serviceName, wrrs);
    				for (Server server : serverList) {
    					serverMap.put(server, serviceName);
    				}
    			}
    		}
    	}
    	Server server = wrrs.getServer();
    	return server;
    }
    
    private void removeOldServer(Set<String> serviceNameSet) {
    	for (String serviceName : serviceNameSet) {
    		WeightedRoundRobinScheduling wrs = serviceNameMap.remove(serviceName);
    		if (wrs != null) {
    			for (Server server : wrs.getServers()) {
    				serverMap.remove(server);
    			}
    			wrs.clear();
    		}
    	}
    }
    
    @Override
    public List<Server> getServerList(String serviceName) {
		List<String> ips = Splitter.on(",").omitEmptyStrings().splitToList(serviceName);
		List<Server> serverList = new ArrayList<>();
		for (String ipAndPort : ips ) {
			Server server = Server.of(ipAndPort);
			if (server != null) {
				serverList.add(server);
			}
		}
		return serverList;
    }
    
    @Override
    public void onConnectionFail(Server server) {
    	String serviceName = serverMap.get(server);
    	if (serviceName != null) {
        	WeightedRoundRobinScheduling weightedRoundRobinScheduling = serviceNameMap.get(serviceName);
        	if (weightedRoundRobinScheduling != null) {
            	if (weightedRoundRobinScheduling.healthilyServers.size() > 1) {
                    weightedRoundRobinScheduling.unhealthilyServers.add(weightedRoundRobinScheduling.getServer(server.getIp(), server.getPort()));
                    weightedRoundRobinScheduling.healthilyServers.remove(weightedRoundRobinScheduling.getServer(server.getIp(), server.getPort()));
            	}
        	}
    	}
    }
}
