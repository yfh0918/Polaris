package com.polaris.distributed.lock;

import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;

import com.polaris.distributed.CouratorFrameworkCreate;

public class CouratorDistributedMutexLock implements DistributedLock {
	private InterProcessMutex lock;

	/**
     * 需要构建的Path
     * @param path path是一个节点
     */
    public CouratorDistributedMutexLock(String url, String path){
    	this(CouratorFrameworkCreate.get(url), path);
    }
    
	/**
     * 传入zk和需要构建的Path
     * @param client Zookeeper客户端连接对象
     * @param path path是一个节点
     */
    public CouratorDistributedMutexLock(CuratorFramework zk, String path){
        lock=new InterProcessMutex(zk,path);
    }
    
    /**获取锁，直到超时，超时后抛出异常*/
    public  void acquire() throws Exception {
		lock.acquire();
    }

    /**
     * 获取锁，带有超时时间
     */
    public boolean acquire(long timeout, TimeUnit unit) throws Exception {
        return lock.acquire(timeout, unit);
    }

    /**释放锁*/
    public void release()throws Exception {
		lock.release();
    }
}
