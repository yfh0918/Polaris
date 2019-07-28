package com.polaris.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.polaris.cache.ehcache.EhCacheCache;
import com.polaris.cache.redis.RedisCache;
import com.polaris.cache.redis.RedisClusterCache;
import com.polaris.core.config.ConfClient;
import com.polaris.core.util.StringUtil;

public class CacheFactory {

	//echache
	private static Map<String, Cache> ehCacheMap = new ConcurrentHashMap<>();

	//redis
	private static Map<String, Cache> redisMap = new ConcurrentHashMap<>();
	
	//redis cluser
	private static Map<String, Cache> redisClusterMap = new ConcurrentHashMap<>();
	
	private CacheFactory() {}
	
	//获取RedisCache
	private static Cache getRedisCache(String cacheName) {
		if (redisMap.get(cacheName) == null) {
    		synchronized(cacheName.intern()) {
    			if (redisMap.get(cacheName) == null) {
    				redisMap.put(cacheName, new RedisCache(cacheName));
    			}
    		}
    	}
    	return redisMap.get(cacheName);
	}
	
	//获取RedisCache
	private static Cache getRedisClusterCache(String cacheName) {
		if (redisClusterMap.get(cacheName) == null) {
    		synchronized(cacheName.intern()) {
    			if (redisClusterMap.get(cacheName) == null) {
    				redisClusterMap.put(cacheName, new RedisClusterCache(cacheName));
    			}
    		}
    	}
    	return redisClusterMap.get(cacheName);
	}
	
	//获取EhCacheCache
	private static Cache getEhCache(String cacheName) {
		if (ehCacheMap.get(cacheName) == null) {
    		synchronized(cacheName.intern()) {
    			if (ehCacheMap.get(cacheName) == null) {
    				ehCacheMap.put(cacheName, new EhCacheCache(cacheName));
    			}
    		}
    	}
    	return ehCacheMap.get(cacheName);
	}
	

	public static Cache getCache(String cacheName) {
		if (StringUtil.isEmpty(cacheName)) {
			return null;
		}
		if (Cache.REDIS.equals(ConfClient.get("cache."+cacheName+".type",Cache.EHCACHE).toLowerCase())) {
			return getRedisCache(cacheName);
		} else if (Cache.REDIS_CLUSTER.equals(ConfClient.get("cache."+cacheName+".type",Cache.EHCACHE).toLowerCase())) {
			return getRedisClusterCache(cacheName);
		}
		return getEhCache(cacheName);
	}
	
}
