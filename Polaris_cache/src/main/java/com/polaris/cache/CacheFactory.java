package com.polaris.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.polaris.cache.ehcache.EhCacheCache;
import com.polaris.cache.memcache.MemCache;
import com.polaris.cache.redis.RedisCache;
import com.polaris.comm.config.ConfClient;
import com.polaris.comm.util.StringUtil;

public class CacheFactory {

	//echache
	private static Map<String, Cache> ehCacheMap = new ConcurrentHashMap<>();

	//redis
	private static Map<String, Cache> redisMap = new ConcurrentHashMap<>();
	
	//memcached
	private static Map<String, Cache> memCacheMap = new ConcurrentHashMap<>();
	
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
	
	//获取MemCache
	private static Cache getMemCache(String cacheName) {
		if (memCacheMap.get(cacheName) == null) {
    		synchronized(cacheName.intern()) {
    			if (memCacheMap.get(cacheName) == null) {
    				memCacheMap.put(cacheName, new MemCache(cacheName));
    			}
    		}
    	}
    	return memCacheMap.get(cacheName);
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
		} else if (Cache.MEMCACHED.equals(ConfClient.get("cache."+cacheName+".type",Cache.EHCACHE).toLowerCase())) {
			return getMemCache(cacheName);
		}
		return getEhCache(cacheName);
	}
	
}
