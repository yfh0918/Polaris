package com.polaris.cache;

import java.util.List;

public interface Cache {
	//cache参数
	public static final String CACHE_DEFAULT_NAME="PolarisCache";
	public static final String CACHE_MAX_SIZE_DEFAULT_VALUE = "10000";
	public static final String EHCACHE = "ehcache";
	public static final String REDIS = "redis";
	public static final String REDIS_CLUSTER = "rediscluster";
	public static final String MEMCACHED = "memcached";
	
	void put(Object key, Object value, int timeout);
	void put(Object key, Object value);
	void remove(Object key);
	void removeAll();
	Object get(Object key);
	@SuppressWarnings("rawtypes")
	default List getKeys() {
		return null;
	}
}
