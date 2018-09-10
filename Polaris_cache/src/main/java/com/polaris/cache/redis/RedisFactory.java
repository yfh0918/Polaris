package com.polaris.cache.redis;

import com.polaris.comm.config.ConfClient;
import com.polaris.comm.util.StringUtil;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisFactory {
	
	private static String host;
	
    private static int port = 6379;

    private static String password;
    
    //可用连接实例的最大数目，默认值为8；
    //如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
    private static int maxActive = 8;
    
    //控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8。
    private static int maxIdle = 8;
    
    //等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException；
    private static int maxWait = 10000;
	
    private static int timeout = 10000;
    
    //在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
    private static boolean testOnBorrow = true;
	private static JedisPool jedisPool = null;
	
	//初期化jedisPool
	private static void iniJedisPool () {
		host = ConfClient.get("redis.host","");
		password = ConfClient.get("redis.password","");
		
		String portStr = ConfClient.get("redis.port","");
		port = (StringUtil.isNotEmpty(portStr)) ? Integer.valueOf(portStr): port;
		
		String maxActiveStr = ConfClient.get("redis.maxActive","");
		maxActive = (StringUtil.isNotEmpty(maxActiveStr)) ? Integer.valueOf(maxActiveStr): maxActive;
		
		String maxIdleStr = ConfClient.get("redis.maxIdle","");
		maxIdle = (StringUtil.isNotEmpty(maxIdleStr)) ? Integer.valueOf(maxIdleStr): maxIdle;
		
		String maxWaitStr = ConfClient.get("redis.maxWait","");
		maxWait = (StringUtil.isNotEmpty(maxWaitStr)) ? Integer.valueOf(maxWaitStr): maxWait;
		
		String timeoutStr = ConfClient.get("redis.timeout","");
		timeout = (StringUtil.isNotEmpty(timeoutStr)) ? Integer.valueOf(timeoutStr): timeout;
		
		String testOnBorrowStr = ConfClient.get("redis.testOnBorrow","");
		testOnBorrow = (StringUtil.isNotEmpty(testOnBorrowStr)) ? Boolean.valueOf(testOnBorrowStr): testOnBorrow;
		
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(maxActive);
		config.setMaxIdle(maxIdle);
		config.setMaxWaitMillis(maxWait);
		config.setTestOnBorrow(testOnBorrow);
		if (StringUtil.isEmpty(password)) {
			jedisPool = new JedisPool(config, host, port, timeout);
		} else {
			jedisPool = new JedisPool(config, host, port, timeout, password);
		}
	}
	
	//禁止new方式创建
	private RedisFactory() {
	}

    
	/**
     * 获取Jedis实例
     * 
     * @return
     */
    public static Jedis getJedis() {
    	if (jedisPool == null) {
    		synchronized(RedisFactory.class) {
    			if (jedisPool == null) {
    				iniJedisPool();
    			}
    		}
    	}
    	return jedisPool.getResource();
    }

	 /**
	  * 释放jedis资源
	  * 
	  * @param jedis
	  */
	 public synchronized void release() {
	     if (jedisPool != null) {
	         jedisPool.close();
	         jedisPool = null;
	     }
	 }
    
}
