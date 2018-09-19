package com.polaris.gateway;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.littleshoot.proxy.HostResolver;

import com.polaris.comm.config.ConfClient;
import com.polaris.comm.thread.ServerCheckTask;
import com.polaris.comm.util.PropertyUtils;
import com.polaris.comm.util.WeightedRoundRobinScheduling;
import com.polaris.gateway.util.PropertiesUtil;

/**
 * @author:Tom.Yu
 *
 * Description:
 *
 */
public class HostResolverImpl implements HostResolver {
    private volatile static HostResolverImpl singleton;
    private volatile Map<String, WeightedRoundRobinScheduling> serverMap = new ConcurrentHashMap<>();
    private volatile Map<String, String> uriMap = new ConcurrentHashMap<>();
    public static final String UPSTREAM = GatewayConstant.config + File.separator +"application_upstream.properties";
    private volatile long lastModified;

    //载入需要代理的IP(需要动态代理)
    public void watchUpstream(boolean isAlways) {
		long currentFileModified;
		try {
			currentFileModified = new File(PropertyUtils.getFilePath(UPSTREAM)).lastModified();
		} catch (IOException e) {
			currentFileModified = -1l;
		}
    	if (!isAlways) {
    		if (currentFileModified == lastModified) {
    			return;
    		}
    	}
    	lastModified = currentFileModified;

    	Map<String, WeightedRoundRobinScheduling> tempServerMap = new ConcurrentHashMap<>();
    	Map<String, String> tempUriMap = new ConcurrentHashMap<>();
    	Map<String, String> servers = PropertiesUtil.getProperty(UPSTREAM);
        for (Map.Entry<String, String> entry : servers.entrySet()) {
            String hostInfo = entry.getKey();
            
            if (hostInfo.startsWith(GatewayConstant.PORT) || GatewayConstant.DEFAULT.equals(hostInfo)) {
            	String[] serversInfo = entry.getValue().split(",");
                List<WeightedRoundRobinScheduling.Server> serverList = new ArrayList<>();
                for (String serverInfo : serversInfo) {
                    String[] si = serverInfo.split(":");
                    if (si.length == 2) {
                        WeightedRoundRobinScheduling.Server server = new WeightedRoundRobinScheduling.Server(si[0], Integer.valueOf(si[1]), 1);
                        serverList.add(server);
                    } else if (si.length == 3) {
                        WeightedRoundRobinScheduling.Server server = new WeightedRoundRobinScheduling.Server(si[0], Integer.valueOf(si[1]), Integer.valueOf(si[2]));
                        serverList.add(server);
                    }
                }
                if (hostInfo.startsWith(GatewayConstant.PORT)) {
                    tempServerMap.put(hostInfo.substring(GatewayConstant.PORT.length()), new WeightedRoundRobinScheduling(serverList));
                } else {
                    tempServerMap.put(GatewayConstant.SERVER_PORT, new WeightedRoundRobinScheduling(serverList));
                }
            } else {
            	tempUriMap.put(hostInfo, entry.getValue());
            }
        }
    	serverMap = tempServerMap;
    	uriMap = tempUriMap;
    }
    
    //构造函数（单例）
    private HostResolverImpl() {
    	watchUpstream(true);//载入配置文件
        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
        scheduledThreadPoolExecutor.scheduleAtFixedRate(new ServerCheckTask(serverMap), 
        		Integer.parseInt(ConfClient.get("gateway.fail.timeout")), 
        		Integer.parseInt(ConfClient.get("gateway.fail.timeout")), TimeUnit.SECONDS);
    }
    public static HostResolverImpl getSingleton() {
        if (singleton == null) {
            synchronized (HostResolverImpl.class) {
                if (singleton == null) {
                    singleton = new HostResolverImpl();
                }
            }
        }
        return singleton;
    }

    //获取服务
    WeightedRoundRobinScheduling getServers(String key) {
    	return serverMap.get(key);
    }
    String getPort(String uri) {
    	if (uri != null) {
        	for (Entry<String, String> entry : uriMap.entrySet()) {
        		if (uri.startsWith(entry.getKey())) {
        			return entry.getValue();
        		}
        		
        	}    	
        }
    	return GatewayConstant.SERVER_PORT;
    }

    @Override
    public InetSocketAddress resolve(String host, int port)
            throws UnknownHostException {
    	
    	String key = String.valueOf(port);
        if (serverMap.containsKey(key)) {
            WeightedRoundRobinScheduling.Server server = serverMap.get(key).getServer();
            if (server != null) {
                return new InetSocketAddress(server.getIp(), server.getPort());
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}
