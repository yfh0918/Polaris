package com.polaris.extension.cache.redis;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.polaris.core.config.ConfClient;
import com.polaris.core.util.JacksonUtil;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.util.JedisClusterCRC16;

/**
 * @author qijian
 * @ClassName: RedisUtil
 * @Description: Redis 集群工具类
 * @date 2017年11月16日 上午11:54:39
 */
public class RedisClusterUtil {

	
//	redis.address1=ip1:port1
//	redis.address2=ip2:port2
//	redis.address3=ip3:port3
//	redis.address4=ip4:port4
//	redis.address5=ip5:port5
//	redis.address6=ip6:port6
//	redis.password=xxxxx
//	REDIS_USER_SESSION_KEY=REDIS_USER_SESSION
//	SSO_SESSION_EXPIRE=86400
	
    private static Logger LOGGER = LoggerFactory.getLogger(RedisClusterUtil.class);

    private static int TIMEOUT = 2000;

    // 最多重定向次数
    private static int MAXATTEMPTS = 100;

    private static JedisCluster jedisCluster;

    private static String addressKeyPrefix = "redis.address";

    private static Pattern pattern = Pattern.compile("^.+[:]\\d{1,5}\\s*$");

    static {
        if (null == jedisCluster) {
            initialJedisCluster();
        }
    }

    /**
     * 读取配置文件中的所有HostAndPort
     */
    private static Set<HostAndPort> parseHostAndPort(Properties properties) throws Exception {
        try {
            Set<HostAndPort> haps = new HashSet<HostAndPort>();
            for (Object key : properties.keySet()) {

                if (!((String) key).startsWith(addressKeyPrefix)) {
                    continue;
                }

                String val = (String) properties.get(key);

                boolean isIpPort = pattern.matcher(val).matches();

                if (!isIpPort) {
                    throw new IllegalArgumentException("ip 或 port 不合法");
                }
                String[] ipAndPort = val.split(":");

                HostAndPort hap = new HostAndPort(ipAndPort[0].trim(), Integer.parseInt(ipAndPort[1].trim()));
                haps.add(hap);
            }

            return haps;
        } catch (IllegalArgumentException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new Exception("解析 jedis 配置文件失败", ex);
        }
    }

    /**
     * 初始化jedisCluster 加锁防止多线程不安全
     */
    private static synchronized void initialJedisCluster() {
        try {
            if (null == jedisCluster) {
                Properties properties = loadProperties();
                String password = properties.getProperty("redis.password");

                JedisPoolConfig config = new JedisPoolConfig();
                config.setMaxTotal(300);
                config.setMaxIdle(50);
                config.setMinIdle(20);
                config.setMaxWaitMillis(10 * 1000);
                config.setTestOnBorrow(true);

                // Redis集群的节点集合
                Set<HostAndPort> jedisClusterNodes = parseHostAndPort(properties);
                // 集群各节点集合，超时时间，超时时间，最多重定向次数，密码，链接池
                jedisCluster = new JedisCluster(jedisClusterNodes, TIMEOUT, TIMEOUT, MAXATTEMPTS, password, config);
                LOGGER.info("First create jedisCluster success");
            }
        } catch (Exception e) {
            LOGGER.error("First create jedisCluster error : {}", e);
            returnBrokenResource(jedisCluster);
        }
    }
    private static Properties loadProperties() {
    	Properties properties = new Properties();
    	int count = Integer.parseInt(ConfClient.get("redis.count","6"));
    	for (int index = 1; index <= count; index++) {
    		String key = "redis.address" + index;
    		properties.setProperty(key, ConfClient.get(key));
    	}
		properties.setProperty("redis.password", ConfClient.get("redis.password"));
		properties.setProperty("REDIS_USER_SESSION_KEY", ConfClient.get("REDIS_USER_SESSION_KEY"));
		properties.setProperty("SSO_SESSION_EXPIRE", ConfClient.get("SSO_SESSION_EXPIRE"));    	
    	return properties;
    }

    /**
     * 回收Jedis对象资源
     *
     * @param jedis
     */
    public static synchronized void returnResource(JedisCluster jedisCluster) {
        if (jedisCluster != null) {
            Map<String, JedisPool> poolMap = jedisCluster.getClusterNodes();
            for (String eachKey : poolMap.keySet()) {
                JedisPool eachPool = poolMap.get(eachKey);
                // eachPool.returnResource(eachPool.getResource());
                eachPool.getResource().close();
            }

            // poolMap.forEach((eachKey, eachPool) -> {
            // eachPool.returnResource(eachPool.getResource());
            // });
        }
    }

    /**
     * Jedis对象出异常的时候，回收Jedis对象资源
     *
     * @param jedis
     */
    public static synchronized void returnBrokenResource(JedisCluster jedisCluster) {
        // if (jedisCluster != null) {
        // Map<String, JedisPool> poolMap = jedisCluster.getClusterNodes();
        // poolMap.forEach((eachKey, eachPool) -> {
        // // eachPool.returnBrokenResource(eachPool.getResource());
        // eachPool.getResource().close();
        // });
        // }
    }

    /**
     * 在多线程环境同步初始化
     */
    public static JedisCluster getJedisCluster() {
        if (null == jedisCluster) {
            initialJedisCluster();
        }
        return jedisCluster;
    }

    /**
     * 设置 String
     *
     * @param key
     * @param value
     */
    public static String set(String key, String value) {
        String ans = null;
        try {
            value = StringUtils.isBlank(value) ? "" : value;
            ans = getJedisCluster().set(key, value);
        } catch (Exception e) {
            LOGGER.error("Set key error : {}", e);
        } finally {
            returnBrokenResource(jedisCluster);
            returnResource(jedisCluster);
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
        String ans = null;
        try {
            ans = getJedisCluster().set(key, value);
        } catch (Exception e) {
            LOGGER.error("Set key error : {}", e);
        } finally {
            returnBrokenResource(jedisCluster);
            returnResource(jedisCluster);
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
        String ans = null;
        try {
            value = StringUtils.isBlank(value) ? "" : value;
            ans = getJedisCluster().setex(key, seconds, value);
        } catch (Exception e) {
            LOGGER.error("Set keyex error : {}", e);
        } finally {
            returnBrokenResource(jedisCluster);
            returnResource(jedisCluster);
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
        JedisCluster jedis = null;
        String ans = null;
        try {
            jedis = getJedisCluster();
            ans = jedis.set(key, value);
            jedis.expire(key, seconds);
        } catch (Exception e) {
            LOGGER.error("Set key error : {}", e);
        } finally {
            returnBrokenResource(jedisCluster);
            returnResource(jedisCluster);
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
        try {
            value = getJedisCluster().get(key);
        } catch (Exception e) {
            LOGGER.error("Get value error : {}", e);
        } finally {
            returnBrokenResource(jedisCluster);
            returnResource(jedisCluster);
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
            result = JacksonUtil.toObj(value, clazz);
        } catch (Exception e) {
            LOGGER.error("Get clazz value error : {}", e);
        } finally {
            returnBrokenResource(jedisCluster);
            returnResource(jedisCluster);
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
        byte[] value = null;
        try {
            value = getJedisCluster().get(key);
        } catch (Exception e) {
            LOGGER.error("Get byte value error : {}", e);
        } finally {
            returnBrokenResource(jedisCluster);
            returnResource(jedisCluster);
        }
        return value;
    }

    /**
     * 删除单个key
     *
     * @param keys
     */
    public static Long deleteKey(String keys) {
        Long flag = null;
        try {
            flag = getJedisCluster().del(keys);
        } catch (Exception e) {
            LOGGER.error("Remove keyex error : {}", e);
        } finally {
            returnBrokenResource(jedisCluster);
            returnResource(jedisCluster);
        }
        return flag;
    }
    public static Long deleteKey(byte[] keys) {
        Long flag = null;
        try {
            flag = getJedisCluster().del(keys);
        } catch (Exception e) {
            LOGGER.error("Remove keyex error : {}", e);
        } finally {
            returnBrokenResource(jedisCluster);
            returnResource(jedisCluster);
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
    public static void deleteKeys(String keysPattern) {
        Map<String, JedisPool> clusterNodes = getJedisCluster().getClusterNodes();

        for (Map.Entry<String, JedisPool> entry : clusterNodes.entrySet()) {
            Jedis jedis = entry.getValue().getResource();
            if (!jedis.info("replication").contains("role:slave")) {
                Set<String> keys = jedis.keys(keysPattern);

                if (keys.size() > 0) {
                    Map<Integer, List<String>> map = new HashMap<>(6600);
                    for (String key : keys) {
                        // cluster模式执行多key操作的时候，这些key必须在同一个slot上，不然会报:JedisDataException:
                        // CROSSSLOT Keys in request don't hash to the same slot
                        int slot = JedisClusterCRC16.getSlot(key);
                        // 按slot将key分组，相同slot的key一起提交
                        if (map.containsKey(slot)) {
                            map.get(slot).add(key);
                        } else {
                            map.put(slot, Lists.newArrayList(key));
                        }
                    }
                    for (Map.Entry<Integer, List<String>> integerListEntry : map.entrySet()) {
                        jedis.del(integerListEntry.getValue().toArray(new String[integerListEntry.getValue().size()]));
                    }
                    jedis.close();
                }
            }
        }
    }

    /**
     * 查询包含{参数}的所有key<br>
     * jedisCluster没有提供对keys命令的封装，只能自己实现
     *
     * @param pattern
     */

    public static TreeSet<String> keys(String pattern) {
        TreeSet<String> keys = new TreeSet<>();
        Map<String, JedisPool> clusterNodes = getJedisCluster().getClusterNodes();
        for (String k : clusterNodes.keySet()) {
            JedisPool jp = clusterNodes.get(k);
            Jedis connection = jp.getResource();
            try {
                keys.addAll(connection.keys(pattern));
            } catch (Exception e) {
                LOGGER.error("Getting keys error: {}", e);
            } finally {
                connection.close();// 用完一定要close这个链接！！！
            }
        }
        return keys;
    }

    public static boolean exists(String key) {
        boolean flag = false;
        try {
            flag = getJedisCluster().exists(key);
        } catch (Exception e) {
            LOGGER.error("exists error : {}", e);
            // returnBrokenResource(jedisCluster);
            // returnResource(jedisCluster);
        } finally {
            returnBrokenResource(jedisCluster);
            returnResource(jedisCluster);
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
        try {
            getJedisCluster().lpush(key, strings);
        } catch (Exception e) {
            LOGGER.error("lpush error : {}", e);
        } finally {
            returnBrokenResource(jedisCluster);
            returnResource(jedisCluster);
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
        try {
            getJedisCluster().lrem(key, count, value);
        } catch (Exception e) {
            LOGGER.error("lrem error : {}", e);
        } finally {
            returnBrokenResource(jedisCluster);
            returnResource(jedisCluster);
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
        JedisCluster jedis = null;
        try {
            jedis = getJedisCluster();
            jedis.sadd(key, value);
            jedis.expire(key, seconds);
        } catch (Exception e) {
            LOGGER.error("sadd error : {}", e);
        } finally {
            returnBrokenResource(jedisCluster);
            returnResource(jedisCluster);
        }
    }

    /**
     * incr
     *
     * @param key
     * @return value
     */
    public static Long incr(String key) {
        Long value = null;
        try {
            value = getJedisCluster().incr(key);
        } catch (Exception e) {
            LOGGER.error("incr error : {}", e);
        } finally {
            returnBrokenResource(jedisCluster);
            returnResource(jedisCluster);
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
        Long value = null;
        try {
            value = getJedisCluster().decr(key);
        } catch (Exception e) {
            LOGGER.error("decr error : {}", e);
        } finally {
            returnBrokenResource(jedisCluster);
            returnResource(jedisCluster);
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
        Long result = null;
        try {
            result = getJedisCluster().expire(key, second);
        } catch (Exception e) {
            LOGGER.error("expire error : {}", e);
        } finally {
            returnBrokenResource(jedisCluster);
            returnResource(jedisCluster);
        }
        return result;
    }

    /**
     * 设置 key的过期时间
     *
     * @param key
     * @param seconds 以秒为单位
     */
    public static Long ttl(String key) {
        Long result = null;
        try {
            result = getJedisCluster().ttl(key);
        } catch (Exception e) {
            LOGGER.error("expire error : {}", e);
        } finally {
            returnBrokenResource(jedisCluster);
            returnResource(jedisCluster);
        }
        return result;
    }
}