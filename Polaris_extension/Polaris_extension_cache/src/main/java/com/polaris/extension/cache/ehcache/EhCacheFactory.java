package com.polaris.extension.cache.ehcache;

import com.polaris.core.config.ConfClient;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;

public class EhCacheFactory {
	
	private static CacheManager cacheManager= CacheManager.create();
		
	//初期化
	private static void iniEhCache (String cacheName, int maxSize) {
		CacheConfiguration cacheConf = new CacheConfiguration(cacheName,maxSize);//创建一个叫pedCache的缓存
		cacheConf.memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LFU);
		Cache cache = new Cache(cacheConf);
		cacheManager.addCache(cache);
	}
	
	//禁止new方式创建
	private EhCacheFactory() {
	}
	
	/**
     * 获取Ehcache实例
     * 
     * @return
     */
    public static net.sf.ehcache.Cache getEhCache() {
    	return getEhCache(com.polaris.extension.cache.Cache.CACHE_DEFAULT_NAME);
    }
    public static net.sf.ehcache.Cache getEhCache(String cacheName) {
		int maxSize = Integer.parseInt(ConfClient.get("cache."+cacheName+".maxsize", com.polaris.extension.cache.Cache.CACHE_MAX_SIZE_DEFAULT_VALUE));
		return getEhCache(cacheName, maxSize);
    }
    public static net.sf.ehcache.Cache getEhCache(String cacheName, int maxSize) {
    	if (cacheManager.getCache(cacheName) == null) {
    		synchronized(EhCacheFactory.class) {
    			if (cacheManager.getCache(cacheName)  == null) {
    				iniEhCache(cacheName, maxSize);
    			}
    		}
    	}
    	return cacheManager.getCache(cacheName);
    }
}
