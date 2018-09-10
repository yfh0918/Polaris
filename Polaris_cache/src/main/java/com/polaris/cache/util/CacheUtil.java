package com.polaris.cache.util;

import java.io.UnsupportedEncodingException;

import com.polaris.cache.Cache;
import com.polaris.cache.ehcache.EhCacheCache;
import com.polaris.cache.serializer.KryoSerializer;
import com.polaris.cache.serializer.ObjectSerializer;
import com.polaris.comm.Constant;

public class CacheUtil {
	
	//获取缓存
    public static <T> T getCacheObject(Cache cache, Object key) { 
    	return getCacheObject(cache, key, false);  
    }
    public static <T> T getCacheObject(Cache cache, Object key, boolean serialize) { 
    	return getCacheObject(cache, key, serialize, getSerializer());  
    }
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> T getCacheObject(Cache cache, Object key, boolean serialize, ObjectSerializer serializer) { 
    	if (key == null) {
    		return null;
    	}
    	Object cacheObject = null;
    	if (isSerialize(cache,serialize)) {
			try {
				cacheObject = cache.get(key.toString().getBytes(Constant.UTF_CODE));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return null;
			}
            if (cacheObject != null) {
                cacheObject = serializer.deserialize((byte[]) cacheObject);  
            }
    	} else {
            cacheObject = cache.get(key); 
    	}
        return (T)cacheObject;  
    }
    @SuppressWarnings("rawtypes")
	public static ObjectSerializer getSerializer() {
    	return new KryoSerializer();
    }
    
    //存放缓存
	public  static void cacheObject(Cache cache, Object key, Object value, int... timeout) {
		cacheObject(cache, key, value, false, timeout);
    } 
	public  static void cacheObject(Cache cache, Object key, Object value, boolean serialize, int... timeout) {
		cacheObject(cache, key, value, serialize, getSerializer(), timeout);
    } 
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public  static void cacheObject(Cache cache, Object key, Object value, boolean serialize, ObjectSerializer serializer ,int... timeout) {
    	if (key == null || value == null) {
    		return;
    	}
    	
    	//Ehcache不需要序列化之后缓存
        if (isSerialize(cache,serialize)) {  
            value = serializer.serialize(value);  
            if (timeout != null && timeout.length > 0) {
				try {
					cache.put(key.toString().getBytes(Constant.UTF_CODE), value, timeout[0]);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					return;
				}
            } else {
                try {
					cache.put(key.toString().getBytes(Constant.UTF_CODE), value);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					return;
				}  
            }
        } else {
        	if (timeout != null && timeout.length > 0) {
                cache.put(key, value, timeout[0]);  
            } else {
                cache.put(key, value);  
            }
        }
    }
    
    //删除缓存
    public static void removeCacheObject(Cache cache, Object key) { 
    	removeCacheObject(cache, key, false);
    } 
    public static void removeCacheObject(Cache cache, Object key, boolean serialize) { 
    	if (key == null) {
    		return;
    	}
    	if (isSerialize(cache,serialize)) {  
        	try {
				cache.remove(key.toString().getBytes(Constant.UTF_CODE));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return;
			}  
    	} else {
        	cache.remove(key);  
    	}
    } 
    
    //判断是否序列化
    private static boolean isSerialize(Cache cache, boolean serialize) {
    	if (serialize) {
        	//Ehcache不需要序列化
    		if (!(cache instanceof EhCacheCache)) {
    			return true;
    		}
    	} 
    	return false;
    }
}
