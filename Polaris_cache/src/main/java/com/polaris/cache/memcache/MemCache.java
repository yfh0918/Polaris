package com.polaris.cache.memcache;

import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.whalin.MemCached.MemCachedClient;
import com.polaris.cache.Cache;
import com.polaris.core.Constant;

public class MemCache implements Cache {

	private String cacheName;//用于区分不同缓存名称但是key相同的元素
	public MemCache(String cacheName) {
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
		MemCachedClient memCached = MemcacheFactory.getMemCached();
		if (key instanceof byte[]) {
			try {
				key = new String((byte[])key,Constant.UTF_CODE);
			} catch (UnsupportedEncodingException e) {
				e.fillInStackTrace();
				return;
			}
		}
		key = cacheName + key.toString();
		//insert
		if (!memCached.keyExists(key.toString())) {
			if (timeout > 0) {
				memCached.set(key.toString(), (byte[])value, timeout);
			} else {
				memCached.set(key.toString(), (byte[])value);
			}
		
		//update
		} else {
			if (timeout > 0) {
				memCached.replace(key.toString(), (byte[])value, timeout);
			} else {
				memCached.replace(key.toString(), (byte[])value);
			}
		}
	}
	@Override
	public void remove(Object key) {
		if (key == null) {
			return;
		}
		MemCachedClient memCached = MemcacheFactory.getMemCached();
		if (key instanceof byte[]) {
			try {
				key = new String((byte[])key,Constant.UTF_CODE);
			} catch (UnsupportedEncodingException e) {
				e.fillInStackTrace();
				return;
			}
		}
		key = cacheName + key.toString();
		memCached.delete(key.toString());
	}
	
	@Override
	public void removeAll() {
		MemCachedClient memCachedClient = MemcacheFactory.getMemCached();
	    Set<String> list = new HashSet<>();
	    Map<String, Map<String, String>> items = memCachedClient.statsItems();
	    for (Iterator<String> itemIt = items.keySet().iterator(); itemIt.hasNext();) {
	        String itemKey = itemIt.next();
	        Map<String, String> maps = items.get(itemKey);
	        for (Iterator<String> mapsIt = maps.keySet().iterator(); mapsIt.hasNext();) {
	            String mapsKey = mapsIt.next();
	            String mapsValue = maps.get(mapsKey);
                String[] arr = mapsKey.split(":");
                int slabNumber = Integer.valueOf(arr[1].trim());
                int limit = Integer.valueOf(mapsValue.trim());
                Map<String, Map<String, String>> dumpMaps = memCachedClient.statsCacheDump(slabNumber, limit);
                for (Iterator<String> dumpIt = dumpMaps.keySet().iterator(); dumpIt.hasNext();) {
                    String dumpKey = dumpIt.next();
                    Map<String, String> allMap = dumpMaps.get(dumpKey);
                    for (Iterator<String> allIt = allMap.keySet().iterator(); allIt.hasNext();) {
                        String allKey = allIt.next();
                        if (allKey.startsWith(cacheName)) {
                        	list.add(allKey.trim());
                        }
                        
                    }
                }
	        }
	    }
	    for (String key : list) {
	    	memCachedClient.delete(key);
	    }
	    list.clear();
	}
	
	@Override
	public Object get(Object key) {
		if (key == null) {
			return null;
		}
		MemCachedClient memCached = MemcacheFactory.getMemCached();
		if (key instanceof byte[]) {
			try {
				key = new String((byte[])key,Constant.UTF_CODE);
			} catch (UnsupportedEncodingException e) {
				e.fillInStackTrace();
				return null;
			}
		}
		key = cacheName + key.toString();
		return memCached.get(key.toString());
	}
}
