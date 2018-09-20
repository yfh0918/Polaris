package com.polaris.core.connect;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import com.polaris.comm.util.LogUtil;
import com.polaris.comm.util.WeightedRoundRobinScheduling;

public class ServerCheckTask implements Runnable {
	
	private static LogUtil logger = LogUtil.getInstance(ServerCheckTask.class);
    private final HttpClient client = HttpClientBuilder.create().build();
    private Map<String, WeightedRoundRobinScheduling> serverMap = new ConcurrentHashMap<>();

    public ServerCheckTask(Map<String, WeightedRoundRobinScheduling> serverMap) {
    	this.serverMap = serverMap;
    }
    
    @Override
    public void run() {
        try {
            for (Map.Entry<String, WeightedRoundRobinScheduling> entry : serverMap.entrySet()) {
                WeightedRoundRobinScheduling weightedRoundRobinScheduling = entry.getValue();
                List<WeightedRoundRobinScheduling.Server> delServers = new ArrayList<>();
                CloseableHttpResponse httpResponse = null;
                for (WeightedRoundRobinScheduling.Server server : weightedRoundRobinScheduling.unhealthilyServers) {
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
