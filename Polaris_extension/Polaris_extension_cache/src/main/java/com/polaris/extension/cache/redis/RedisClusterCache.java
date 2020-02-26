package com.polaris.extension.cache.redis;

import java.io.UnsupportedEncodingException;

import com.polaris.core.Constant;
import com.polaris.extension.cache.Cache;

public class RedisClusterCache implements Cache {

	private String cacheName;//用于区分不同缓存名称但是key相同的元素
	public RedisClusterCache(String cacheName) {
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
		if (key instanceof byte[]) {
			try {
				key = (cacheName + new String((byte[])key,Constant.UTF_CODE)).getBytes(Constant.UTF_CODE);
			} catch (UnsupportedEncodingException e) {
				e.fillInStackTrace();
				return;
			}
			if (timeout > 0) {
				RedisClusterUtil.set((byte[])key, (byte[])value, timeout);
			} else {
				RedisClusterUtil.set((byte[])key, (byte[])value);
			}
		} else {
			key = cacheName + key.toString();
			if (timeout > 0) {
				RedisClusterUtil.set(key.toString(), value.toString(), timeout);
			} else {
				RedisClusterUtil.set(key.toString(), value.toString());
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
			RedisClusterUtil.deleteKey((byte[])key);
		} else {
			key = cacheName + key.toString();
			RedisClusterUtil.deleteKey(key.toString());
		}
		
		return;
	}
	
	@Override
	public void removeAll() {
		RedisClusterUtil.deleteKeyByPrefix(cacheName);
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
			result = RedisClusterUtil.get((byte[])key);
		} else {
			key = cacheName + key.toString();
			result = RedisClusterUtil.get(key.toString());
		}
		
		return result;
	}
	
}
