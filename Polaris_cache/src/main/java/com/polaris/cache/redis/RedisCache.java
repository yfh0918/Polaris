package com.polaris.cache.redis;

import java.io.UnsupportedEncodingException;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import com.polaris.cache.Cache;
import com.polaris.core.Constant;

import redis.clients.jedis.Jedis;

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
		Jedis jedis = RedisFactory.getJedis();
		if (key instanceof byte[]) {
			try {
				key = (cacheName + new String((byte[])key,Constant.UTF_CODE)).getBytes(Constant.UTF_CODE);
			} catch (UnsupportedEncodingException e) {
				e.fillInStackTrace();
				jedis.close();
				return;
			}
			jedis.set((byte[])key, (byte[])value);
			if (timeout > 0) {
				jedis.expire(key.toString().getBytes(), timeout);
			}
		} else {
			key = cacheName + key.toString();
			jedis.set(key.toString(), value.toString());
			if (timeout > 0) {
				jedis.expire(key.toString(), timeout);
			}
		}
		jedis.close();
	}

	@Override
	public void remove(Object key) {
		if (key == null) {
			return;
		}
		Jedis jedis = RedisFactory.getJedis();
		if (key instanceof byte[]) {
			try {
				key = (cacheName + new String((byte[])key,Constant.UTF_CODE)).getBytes(Constant.UTF_CODE);
			} catch (UnsupportedEncodingException e) {
				jedis.close();
				e.fillInStackTrace();
				return;
			}
			jedis.del((byte[])key);
		} else {
			key = cacheName + key.toString();
			jedis.del(key.toString());
		}
		jedis.close();
		return;
	}
	
	@Override
	public void removeAll() {
		Jedis jedis = RedisFactory.getJedis();
		Set<String> set = jedis.keys(cacheName + "*");
        if (CollectionUtils.isNotEmpty(set)) {
            jedis.del(set.toArray(new String[] {}));
        }
		jedis.close();
		return;
	}
	
	@Override
	public Object get(Object key)  {
		if (key == null) {
			return null;
		}
		Jedis jedis = RedisFactory.getJedis();
		Object result;
		if (key instanceof byte[]) {
			try {
				key = (cacheName + new String((byte[])key,Constant.UTF_CODE)).getBytes(Constant.UTF_CODE);
			} catch (UnsupportedEncodingException e) {
				e.fillInStackTrace();
				jedis.close();
				return null;
			}
			result = jedis.get((byte[])key);
		} else {
			key = cacheName + key.toString();
			result = jedis.get(key.toString());
		}
		jedis.close();
		return result;
	}
	
}
