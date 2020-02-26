package com.polaris.extension.cache.redis;

import java.io.UnsupportedEncodingException;

import com.polaris.core.Constant;
import com.polaris.extension.cache.Cache;

public class RedisCache implements Cache {

	private String cacheName;//用于区分不同缓存名称但是key相同的元素
	public RedisCache(String cacheName) {
		this.cacheName = cacheName + ":";
	}
	
	@Override
	public void put(Object key, Object value) {
		put(key,value,0);//永久不过期
	}

	@Override
	public void put(Object key, Object value, int timeout) {
		if (key == null || value == null) {
			return;
		}
		//Jedis jedis = RedisFactory.getJedis();
		if (key instanceof byte[]) {
			try {
				key = (cacheName + new String((byte[])key,Constant.UTF_CODE)).getBytes(Constant.UTF_CODE);
			} catch (UnsupportedEncodingException e) {
				e.fillInStackTrace();
				return;
			}
			
			if (timeout > 0) {
				RedisSingleUtil.set((byte[])key, (byte[])value, timeout);
			} else {
				RedisSingleUtil.set((byte[])key, (byte[])value);
			}
		} else {
			key = cacheName + key.toString();
			if (timeout > 0) {
				RedisSingleUtil.set(key.toString(), value.toString(), timeout);
			} else {
				RedisSingleUtil.set(key.toString(), value.toString());
			}
		}
	}

	@Override
	public void remove(Object key) {
		if (key == null) {
			return;
		}
		if (key instanceof byte[]) {
			try {
				key = (cacheName + new String((byte[])key,Constant.UTF_CODE)).getBytes(Constant.UTF_CODE);
			} catch (UnsupportedEncodingException e) {
				e.fillInStackTrace();
				return;
			}
			RedisSingleUtil.deleteKey((byte[])key);
		} else {
			key = cacheName + key.toString();
			RedisSingleUtil.deleteKey(key.toString());
		}
		return;
	}
	
	@Override
	public void removeAll() {
		RedisSingleUtil.deleteKeyByPrefix(cacheName);
		return;
	}
	
	@Override
	public Object get(Object key)  {
		if (key == null) {
			return null;
		}
		Object result;
		if (key instanceof byte[]) {
			try {
				key = (cacheName + new String((byte[])key,Constant.UTF_CODE)).getBytes(Constant.UTF_CODE);
			} catch (UnsupportedEncodingException e) {
				e.fillInStackTrace();
				return null;
			}
			result = RedisSingleUtil.get((byte[])key);
		} else {
			key = cacheName + key.toString();
			result = RedisSingleUtil.get(key.toString());
		}
		return result;
	}
	
}
