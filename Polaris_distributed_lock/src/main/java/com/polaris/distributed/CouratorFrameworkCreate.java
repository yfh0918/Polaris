package com.polaris.distributed;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;

import com.polaris.core.config.ConfClient;

public class CouratorFrameworkCreate {
	private static CuratorFramework zk;
	private static RetryNTimes retryNTimesCounter;
    public static CuratorFramework get(
    		int sessionTimeoutMs,
    		int retryCount,int sleepMsBetweenRetries,
    		int connectionTimeoutMs,String url) {
    	if (zk == null) {
    		synchronized(CouratorFrameworkCreate.class) {
    			if (zk == null) {
    		    	int sleepMsBetweenRetriesForCounter = Integer.parseInt(ConfClient.get("zk.counter.sleepMsBetweenRetries", "100"));
    		    	retryNTimesCounter = new RetryNTimes(retryCount,sleepMsBetweenRetriesForCounter);
    				zk= CuratorFrameworkFactory.builder()
    			            .sessionTimeoutMs(sessionTimeoutMs)
    			            .retryPolicy(new RetryNTimes(retryCount,sleepMsBetweenRetries))
    			            .connectionTimeoutMs(connectionTimeoutMs)
    			            .connectString(url)
    			            .build();
    			    zk.start();
    			}
    		}
    	}
	    return zk;
    }
    public static CuratorFramework get(String url) {
    	int sessionTimeoutMs = Integer.parseInt(ConfClient.get("zk.sessionTimeoutMs", "5000"));
    	int retryCount = Integer.parseInt(ConfClient.get("zk.retryCount", "3"));
    	int sleepMsBetweenRetries = Integer.parseInt(ConfClient.get("zk.sleepMsBetweenRetries", "1000"));
    	int connectionTimeoutMs = Integer.parseInt(ConfClient.get("zk.connectionTimeoutMs", "50000"));
    	return get(sessionTimeoutMs,retryCount,sleepMsBetweenRetries,connectionTimeoutMs,url);
    }
    

    public static RetryNTimes getRetryNTimesForCounter(String url) {
    	if (retryNTimesCounter == null) {
    		get(url);
    	}
    	return retryNTimesCounter;
    }
}
