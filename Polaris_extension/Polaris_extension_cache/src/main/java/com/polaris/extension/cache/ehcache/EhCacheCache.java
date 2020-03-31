package com.polaris.extension.cache.ehcache;

import java.io.UnsupportedEncodingException;
import java.util.List;

import com.polaris.core.Constant;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

public class EhCacheCache implements com.polaris.extension.cache.Cache {
	private String cacheName;
	public EhCacheCache(String cacheName) {
		this.cacheName = cacheName;
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
				key = new String((byte[])key,Constant.UTF_CODE);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return;
			}
		}
		net.sf.ehcache.Cache ehcache = EhCacheFactory.getEhCache(cacheName);
		ehcache.acquireWriteLockOnKey(key);
		try {
			Element element = new Element(key, value);
			element.setTimeToIdle(0);
			element.setTimeToLive(timeout);
			ehcache.put(element);
		} finally {
			ehcache.releaseWriteLockOnKey(key);
		} 
	}

	@Override
	public void remove(Object key) {
		if (key == null) {
			return;
		}
		if (key instanceof byte[]) {
			try {
				key = new String((byte[])key,Constant.UTF_CODE);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return;
			}
		}
		net.sf.ehcache.Cache ehcache = EhCacheFactory.getEhCache(cacheName);
		ehcache.remove(key);
	}
	
	@Override
	public void removeAll() {
		net.sf.ehcache.Cache ehcache = EhCacheFactory.getEhCache(cacheName);
		ehcache.removeAll();
	}

	@Override
	public Object get(Object key) {
		if (key == null) {
			return null;
		}
		if (key instanceof byte[]) {
			try {
				key = new String((byte[])key,Constant.UTF_CODE);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return null;
			}
		}		
		net.sf.ehcache.Cache ehcache = EhCacheFactory.getEhCache(cacheName);
		ehcache.acquireReadLockOnKey(key);
		Element element = null;
		try{
			element = ehcache.get(key);
		} finally {
			ehcache.releaseReadLockOnKey(key);
		}
		if (element != null) {
			return element.getObjectValue();
		}
		return null;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public List getKeys() {
		Cache ehcache = EhCacheFactory.getEhCache(cacheName);
		return ehcache.getKeys();
	}
}
