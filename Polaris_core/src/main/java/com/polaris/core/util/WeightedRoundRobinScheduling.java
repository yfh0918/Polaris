package com.polaris.core.util;

import java.math.BigInteger;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import com.polaris.core.pojo.Server;

/**
 * @author:Tom.Yu
 *
 * Description:
 *
 * 权重轮询调度算法(WeightedRound-RobinScheduling)-Java实现
 */
public class WeightedRoundRobinScheduling {
    private int currentIndex = -1;// 上一次选择的服务器
    private int currentWeight = 0;// 当前调度的权值
    public CopyOnWriteArrayList<Server> healthilyServers; //健康服务器集合
    public CopyOnWriteArrayList<Server> unhealthilyServers = new CopyOnWriteArrayList<>(); //不健康服务器集合
    private Map<String, Server> serversMap = new HashMap<>();


    /**
     * 返回最大公约数
     */
    private int gcd(int a, int b) {
        BigInteger b1 = new BigInteger(String.valueOf(a));
        BigInteger b2 = new BigInteger(String.valueOf(b));
        BigInteger gcd = b1.gcd(b2);
        return gcd.intValue();
    }


    /**
     * 返回所有服务器权重的最大公约数
     */
    private int getGCDForServers(List<Server> serverList) {
        int w = 0;
        for (int i = 0, len = serverList.size(); i < len - 1; i++) {
            if (w == 0) {
                w = gcd(serverList.get(i).getWeight(), serverList.get(i + 1).getWeight());
            } else {
                w = gcd(w, serverList.get(i + 1).getWeight());
            }
        }
        return w;
    }

    /**
     * 返回所有服务器中的最大权重
     */
    private int getMaxWeightForServers(List<Server> serverList) {
        int w = 0;
        for (int i = 0, len = serverList.size(); i < len - 1; i++) {
            if (w == 0) {
                w = Math.max(serverList.get(i).getWeight(), serverList.get(i + 1).getWeight());
            } else {
                w = Math.max(w, serverList.get(i + 1).getWeight());
            }
        }
        return w;
    }

    /**
     * 算法流程： 假设有一组服务器 S = {S0, S1, …, Sn-1} 有相应的权重，变量currentIndex表示上次选择的服务器
     * 权值currentWeight初始化为0，currentIndex初始化为-1 ，当第一次的时候返回 权值取最大的那个服务器， 通过权重的不断递减 寻找
     * 适合的服务器返回，直到轮询结束，权值返回为0
     */
    public Server getServer() {
        if (healthilyServers.size() == 0) {
            return null;
        } else if (healthilyServers.size() == 1) {
            return healthilyServers.get(0);
        } else {
            while (true) {
                currentIndex = (currentIndex + 1) % healthilyServers.size();
                if (currentIndex == 0) {
                    currentWeight = currentWeight - getGCDForServers(healthilyServers);
                    if (currentWeight <= 0) {
                        currentWeight = getMaxWeightForServers(healthilyServers);
                        if (currentWeight == 0)
                            return null;
                    }
                }
                if (healthilyServers.get(currentIndex).getWeight() >= currentWeight) {
                    return healthilyServers.get(currentIndex);
                }
            }
        }
    }
    public Server getServer(String ip, int port) {
    	return serversMap.get(ip + "_" + port);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	public WeightedRoundRobinScheduling(List<Server> healthilyServers) {
        this.healthilyServers = new CopyOnWriteArrayList(healthilyServers);
        for (Server server : healthilyServers) {
            serversMap.put(server.getIp() + "_" + server.getPort(), server);
        }
    }
    
    public void add(Server server) {
    	if (server == null) {
    		return;
    	}
    	healthilyServers.add(server);
    	serversMap.put(server.getIp() + "_" + server.getPort(), server);
    }
    
    public void remove(Server server) {
    	if (server == null) {
    		return;
    	}
    	healthilyServers.remove(server);
    	unhealthilyServers.remove(server);
    	serversMap.remove(server.getIp() + "_" + server.getPort());
    }
    
    public Collection<Server> getServers() {
    	return serversMap.values();
    }
    
    public void clear() {
    	healthilyServers.clear();
    	unhealthilyServers.clear();
    	serversMap.clear();
    }
}
