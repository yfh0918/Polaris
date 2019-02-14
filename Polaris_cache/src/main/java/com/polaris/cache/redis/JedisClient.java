package com.polaris.cache.redis;

import java.util.List;
import java.util.Set;

public interface JedisClient {

    String get(String key);

    String set(String key, String value);

    String hget(String hkey, String key);//批量获取指定的值

    long hset(String hkey, String key, String value); // 添加数据

    long incr(String key);

    long expire(String key, int second);

    long ttl(String key);

    long del(String key);

    long hdel(String hkey, String key);// 删除指定的值

    boolean exists(String key);

    Set<String> keys(String pattern);

    Set<String> getAllKeys();

    List<String> hvals(String hkey);

    /**
     * 根据前缀批量删除key
     *
     * @param pre_str
     */
    void batchDel(String pre_str);
}
