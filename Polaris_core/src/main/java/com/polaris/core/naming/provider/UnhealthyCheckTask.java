package com.polaris.core.naming.provider;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.core.pojo.Server;
import com.polaris.core.util.WeightedRoundRobinScheduling;

public class UnhealthyCheckTask implements Runnable {
	
	private static Logger logger = LoggerFactory.getLogger(UnhealthyCheckTask.class);
    private final HttpClient client = HttpClientBuilder.create().build();
    private Map<String, WeightedRoundRobinScheduling> serverMap = new ConcurrentHashMap<>();

    public UnhealthyCheckTask(Map<String, WeightedRoundRobinScheduling> serverMap) {
    	this.serverMap = serverMap;
    }
    
    @Override
    public void run() {
        try {
            for (Map.Entry<String, WeightedRoundRobinScheduling> entry : serverMap.entrySet()) {
                WeightedRoundRobinScheduling weightedRoundRobinScheduling = entry.getValue();
                List<Server> delServers = new ArrayList<>();
                CloseableHttpResponse httpResponse = null;
                for (Server server : weightedRoundRobinScheduling.unhealthilyServers) {
                    HttpGet request = new HttpGet("http://" + server.getIp() + ":" + server.getPort());
                    try {
                        httpResponse = (CloseableHttpResponse) client.execute(request);
                        weightedRoundRobinScheduling.healthilyServers.add(weightedRoundRobinScheduling.getServer(server.getIp(), server.getPort()));
                        delServers.add(server);
                        logger.info("ip->{},port->{} is healthy", server.getIp(), server.getPort());
                    } catch (ConnectException e1) {
                        logger.warn("ip->{},port->{} is unhealthy",  server.getIp(), server.getPort());
                    } catch (Exception e2) {
                        weightedRoundRobinScheduling.healthilyServers.add(weightedRoundRobinScheduling.getServer(server.getIp(), server.getPort()));
                        delServers.add(server);
                        logger.info("ip->{},port->{} is healthy", server.getIp(), server.getPort());
                    } finally {
                        if (httpResponse != null) {
                            httpResponse.close();
                        }
                    }
                }
                if (delServers.size() > 0) {
                    weightedRoundRobinScheduling.unhealthilyServers.removeAll(delServers);
                }
            }
        } catch (Exception e) {
            logger.error("server check task is error", e);
        }
    }
}
