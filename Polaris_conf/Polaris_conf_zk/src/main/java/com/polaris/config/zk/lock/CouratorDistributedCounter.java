package com.polaris.config.zk.lock;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.atomic.AtomicValue;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicLong;
import org.apache.curator.retry.RetryNTimes;

public class CouratorDistributedCounter {
	private DistributedAtomicLong counter;
	private final String ERROR_MESSAGE = "获取失败";

	/**
     * 传入需要构建的Path
     * @param path path是一个节点
     */
    public CouratorDistributedCounter(String url, String path){
    	this(CouratorFrameworkCreate.get(url), path, CouratorFrameworkCreate.getRetryNTimesForCounter(url));
     }

	/**
     * 传入zk的url和需要构建的Path
     * @param client Zookeeper客户端连接对象
     * @param path path是一个节点
     */
    public CouratorDistributedCounter(CuratorFramework zk, String path, RetryNTimes retryNTimesForCounter){
        counter=new DistributedAtomicLong(zk, path, retryNTimesForCounter);
    }
    
    public long addAndGet(long value) throws Exception {
    	
		AtomicValue<Long> returnV = counter.add(value);
		if (returnV.succeeded()) {
			returnV.postValue();
		} 
		throw new Exception(ERROR_MESSAGE);
    }
    
    public long Get() throws Exception {
    	
		AtomicValue<Long> returnV = counter.get();
		if (returnV.succeeded()) {
			returnV.postValue();
		} 
		throw new Exception(ERROR_MESSAGE);
    }
}
