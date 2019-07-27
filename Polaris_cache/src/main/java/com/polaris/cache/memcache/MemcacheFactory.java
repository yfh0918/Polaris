package com.polaris.cache.memcache;

import com.whalin.MemCached.MemCachedClient;
import com.whalin.MemCached.SockIOPool;
import com.polaris.core.config.ConfClient;
import com.polaris.core.util.StringUtil;

public class MemcacheFactory {
	
	// 构建缓存客户端
	private static  MemCachedClient cachedClient;
	
	//初期化jedisPool
	private static void iniMemCachedPool () {
		
		//构造MemCached客户端
		cachedClient = new MemCachedClient();
		
		// 初始化SockIOPool，管理memcached的连接池
		SockIOPool pool = SockIOPool.getInstance();

		// 设置缓存服务器列表，当使用分布式缓存的时，可以指定多个缓存服务器。（这里应该设置为多个不同的服务器）
		String servers = ConfClient.get("memcached.servers","");
		if (StringUtil.isNotEmpty(servers)) {
			pool.setServers(servers.split(","));
		}
		pool.setFailover(true);//容错
		pool.setInitConn(Integer.parseInt(ConfClient.get("memcached.initconn","10"))); // 设置初始连接
		pool.setMinConn(Integer.parseInt(ConfClient.get("memcached.minconn","5"))); // 设置最小连接
		pool.setMaxConn(Integer.parseInt(ConfClient.get("memcached.maxconn","20"))); // 设置最大连接
		pool.setMaxIdle(Integer.parseInt(ConfClient.get("memcached.maxidle",String.valueOf(1000 * 60 * 60 * 3)))); // 设置每个连接最大空闲时间3个小时
		pool.setMaintSleep(Integer.parseInt(ConfClient.get("memcached.maintsleep","3000")));//设置连接池维护线程的睡眠时间
		pool.setNagle(Boolean.parseBoolean(ConfClient.get("memcached.nagle","false")));//设置是否使用Nagle算法（Socket的参数），如果是true在写数据时不缓冲，立即发送出去
		pool.setSocketTO(Integer.parseInt(ConfClient.get("memcached.socketto","3000"))); //设置socket的读取等待超时时间
		pool.setAliveCheck(Boolean.parseBoolean(ConfClient.get("memcached.alivecheck","true")));//#设置连接心跳监测开关
		pool.initialize();
	}
	
	//禁止new方式创建
	private MemcacheFactory() {
	}

	/**
     * 获取Jedis实例
     * 
     * @return
     */
    public static MemCachedClient getMemCached() {
    	if (cachedClient == null) {
    		synchronized(MemcacheFactory.class) {
    			if (cachedClient == null) {
    				iniMemCachedPool();
    			}
    		}
    	}
    	return cachedClient;
    }

}
