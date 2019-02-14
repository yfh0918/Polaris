package com.polaris.cache.redis;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.polaris.comm.config.ConfClient;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author qijian
 * @ClassName: RedisSingleUtil
 * @Description: Redis 单机工具类
 * @date 2017年8月18日 下午5:58:57
 */
public class RedisSingleUtil {

    private static Logger logger = LoggerFactory.getLogger(RedisSingleUtil.class);

    static {
        try {
            IP = ConfClient.get("redis.url");
            PORT = Integer.parseInt(ConfClient.get("redis.port"));
            PASSWORD = ConfClient.get("redis.password");
        } catch (Exception e) {
            logger.error("load redis.properties const error : " + e);

        }
    }

    // Redis服务器IP
    private static String IP;

    // Redis的端口号
    private static int PORT;

    // 访问密码
    private static String PASSWORD;

    // 超时时间
    private static int TIMEOUT = 20000;

    private static JedisPool jedisPool = null;

    /**
     * redis过期时间,以秒为单位
     */
    public final static int EXRP_HOUR = 60 * 60; // 一小时
    public final static int EXRP_DAY = 60 * 60 * 24; // 一天
    public final static int EXRP_MONTH = 60 * 60 * 24 * 30; // 一个月

    /**
     * 初始化Redis连接池
     * 加锁防止多线程不安全
     * 防止创建多个线程池,而最后jedisPool引用的是最后一个线程池,导致前面创建的连接池中的连接无法释放
     */
    private static synchronized void initialPool() {
        try {
            if (null == jedisPool) {
                JedisPoolConfig config = new JedisPoolConfig();
                // 可用连接实例的最大数目，默认值为8；
                // 如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
                config.setMaxTotal(1024);
                // 控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8。
                config.setMaxIdle(200);
                // 设置最小空闲数
                config.setMinIdle(8);
                // 等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException；
                config.setMaxWaitMillis(100000);
                // Idle时进行连接扫描
                config.setTestWhileIdle(true);
                // 表示idle object evitor两次扫描之间要sleep的毫秒数
                config.setTimeBetweenEvictionRunsMillis(30000);
                // 表示idle object evitor每次扫描的最多的对象数
                config.setNumTestsPerEvictionRun(10);
                // 表示一个对象至少停留在idle状态的最短时间，然后才能被idle object
                // evitor扫描并驱逐；这一项只有在timeBetweenEvictionRunsMillis大于0时才有意义
                config.setMinEvictableIdleTimeMillis(60000);
                // 连接耗尽时是否阻塞, false报异常,ture阻塞直到超时, 默认true
                config.setBlockWhenExhausted(false);
                jedisPool = new JedisPool(config, IP, PORT, TIMEOUT, PASSWORD);
                logger.info("First create JedisPool success");
            }
        } catch (Exception e) {
            logger.error("First create JedisPool error : " + e);

        }
    }

    /**
     * 在多线程环境同步初始化
     */
    public static void poolInit() {
        if (null == jedisPool) {
            initialPool();
        }
    }

    /**
     * 同步获取Jedis实例
     *
     * @return Jedis
     */
    public static Jedis getJedis() {
        poolInit();
        Jedis jedis = null;
        try {
            if (null != jedisPool) {
                jedis = jedisPool.getResource();
            }
        } catch (Exception e) {
            returnResource(jedis);
            logger.error("Get jedis error : " + e);

        }
        return jedis;
    }

    /**
     * 释放jedis资源
     *
     * @param jedis
     */
    public static void returnResource(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }

    /**
     * 设置 String
     *
     * @param key
     * @param value
     */
    public static String set(String key, String value) {
        Jedis jedis = null;
        String ans = null;
        try {
            jedis = getJedis();
            value = StringUtils.isBlank(value) ? "" : value;
            ans = jedis.set(key, value);
        } catch (Exception e) {
            if (jedis != null) {
                jedis.close();
            }
            logger.error("Set key error : " + e);

        } finally {
            returnResource(jedis);
        }
        return ans;
    }

    /**
     * 设置 byte[]
     *
     * @param key
     * @param value
     */
    public static String set(byte[] key, byte[] value) {
        Jedis jedis = null;
        String ans = null;
        try {
            jedis = getJedis();
            ans = jedis.set(key, value);
        } catch (Exception e) {
            if (jedis != null) {
                jedis.close();
            }
            logger.error("Set key error : " + e);

        } finally {
            returnResource(jedis);
        }
        return ans;
    }

    /**
     * 设置 String 过期时间
     *
     * @param key
     * @param value
     * @param seconds 以秒为单位
     */
    public static String set(String key, String value, int seconds) {
        Jedis jedis = null;
        String ans = null;
        try {
            value = StringUtils.isBlank(value) ? "" : value;
            jedis = getJedis();
            ans = jedis.setex(key, seconds, value);
        } catch (Exception e) {
            if (jedis != null) {
                jedis.close();
            }
            logger.error("Set keyex error : " + e);

        } finally {
            returnResource(jedis);
        }
        return ans;
    }

    /**
     * 设置 byte[] 过期时间
     *
     * @param key
     * @param value
     * @param seconds 以秒为单位
     */
    public static String set(byte[] key, byte[] value, int seconds) {
        Jedis jedis = null;
        String ans = null;
        try {
            jedis = getJedis();
            ans = jedis.set(key, value);
            jedis.expire(key, seconds);
        } catch (Exception e) {
            if (jedis != null) {
                jedis.close();
            }
            logger.error("Set key error : " + e);

        } finally {
            returnResource(jedis);
        }
        return ans;
    }

    /**
     * 获取String值
     *
     * @param key
     * @return value
     */
    public static String get(String key) {
        String value = null;
        Jedis jedis = null;
        try {
            jedis = getJedis();
            value = jedis.get(key);
        } catch (Exception e) {
            if (jedis != null) {
                jedis.close();
            }
            logger.error("Get value error : " + e);

        } finally {
            returnResource(jedis);
        }
        return value;
    }

    /**
     * 根据key,获取指定类型的值
     *
     * @param key
     * @param clazz
     * @return T
     */
    public static <T> T get(String key, Class<T> clazz) {
        T result = null;
        try {
            String value = get(key);
            result = JSONObject.parseObject(value, clazz);
        } catch (Exception e) {
            logger.error("Get clazz value error : " + e);

        }
        return result;
    }

    /**
     * 获取byte[]值
     *
     * @param key
     * @return value
     */
    public static byte[] get(byte[] key) {
        Jedis jedis = null;
        byte[] value = null;
        try {
            jedis = getJedis();
            value = jedis.get(key);
        } catch (Exception e) {
            if (jedis != null) {
                jedis.close();
            }
            logger.error("Get byte value error : " + e);

        } finally {
            returnResource(jedis);
        }
        return value;
    }

    /**
     * 删除单个key
     *
     * @param keys
     */
    public static Long deleteKey(String keys) {
        Jedis jedis = null;
        Long flag = null;
        try {
            jedis = getJedis();
            flag = jedis.del(keys);
        } catch (Exception e) {
            if (jedis != null) {
                jedis.close();
            }
            logger.error("Remove keyex error : " + e);

        } finally {
            returnResource(jedis);
        }
        return flag;
    }

    /**
     * 删除多个key
     *
     * @param key
     */
    public static Long deleteKey(String[] keys) {
        Jedis jedis = null;
        Long flag = null;
        try {
            jedis = getJedis();
            flag = jedis.del(keys);
        } catch (Exception e) {
            if (jedis != null) {
                jedis.close();
            }
            logger.error("Remove keyex[] error : " + e);

        } finally {
            returnResource(jedis);
        }
        return flag;
    }

    /**
     * 删除前缀为{参数}的所有key<br>
     *
     * @param prefix
     */
    public static void deleteKeyByPrefix(String prefix) {
        deleteKeys(prefix + "*");
    }

    /**
     * 删除包含{参数}的所有key<br>
     *
     * @param contain
     */
    public static void deleteKeyByContain(String contain) {
        deleteKeys("*" + contain + "*");
    }

    /**
     * 批量删除:preStr*
     *
     * @param key
     */
    public static void deleteKeys(String pattern) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            // 列出所有匹配的key
            Set<String> keySet = jedis.keys(pattern);
            if (keySet == null || keySet.size() <= 0) {
                return;
            }
            String keyArr[] = new String[keySet.size()];
            int i = 0;
            for (String keys : keySet) {
                keyArr[i] = keys;
                i++;
            }
            deleteKey(keyArr);
        } catch (Exception e) {
            if (jedis != null) {
                jedis.close();
            }
            logger.error("Remove pattern error : " + e);

        } finally {
            returnResource(jedis);
        }
    }

    public static boolean exists(String key) {
        Jedis jedis = null;
        boolean flag = false;
        try {
            jedis = getJedis();
            flag = jedis.exists(key);
        } catch (Exception e) {
            if (jedis != null) {
                jedis.close();
            }
            logger.error("exists error : " + e);

        } finally {
            returnResource(jedis);
        }
        return flag;

    }

    /**
     * lpush
     *
     * @param key
     * @param key
     */
    public static void lpush(String key, String... strings) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.lpush(key, strings);
        } catch (Exception e) {
            if (jedis != null) {
                jedis.close();
            }
            logger.error("lpush error : " + e);

        } finally {
            returnResource(jedis);
        }
    }

    /**
     * lrem
     *
     * @param key
     * @param count
     * @param value
     */
    public static void lrem(String key, long count, String value) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.lrem(key, count, value);
        } catch (Exception e) {
            if (jedis != null) {
                jedis.close();
            }
            logger.error("lrem error : " + e);

        } finally {
            returnResource(jedis);
        }
    }

    /**
     * sadd
     *
     * @param key
     * @param value
     * @param seconds
     */
    public static void sadd(String key, String value, int seconds) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.sadd(key, value);
            jedis.expire(key, seconds);
        } catch (Exception e) {
            if (jedis != null) {
                jedis.close();
            }
            logger.error("sadd error : " + e);

        } finally {
            returnResource(jedis);
        }
    }

    /**
     * incr
     *
     * @param key
     * @return value
     */
    public static Long incr(String key) {
        Jedis jedis = null;
        Long value = null;
        try {
            jedis = getJedis();
            value = jedis.incr(key);
        } catch (Exception e) {
            if (jedis != null) {
                jedis.close();
            }
            logger.error("incr error : " + e);

        } finally {
            returnResource(jedis);
        }
        return value;
    }

    /**
     * decr
     *
     * @param key
     * @return value
     */
    public static Long decr(String key) {
        Jedis jedis = null;
        Long value = null;
        try {
            jedis = getJedis();
            value = jedis.decr(key);
        } catch (Exception e) {
            if (jedis != null) {
                jedis.close();
            }
            logger.error("decr error : " + e);

        } finally {
            returnResource(jedis);
        }
        return value;
    }

    /**
     * 设置 key的过期时间
     *
     * @param key
     * @param seconds 以秒为单位
     */
    public static Long expire(String key, int second) {
        Jedis jedis = null;
        Long result = null;
        try {
            jedis = getJedis();
            result = jedis.expire(key, second);
        } catch (Exception e) {
            if (jedis != null) {
                jedis.close();
            }
            logger.error("expire error : " + e);

        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 删除当前中所有key
     */
    public static void flushdb() {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.flushDB();
        } catch (Exception e) {
            if (jedis != null) {
                jedis.close();
            }
            logger.error("flushdb error : " + e);

        } finally {
            returnResource(jedis);
        }
    }
}