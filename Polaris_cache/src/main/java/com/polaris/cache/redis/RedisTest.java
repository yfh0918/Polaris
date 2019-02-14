package com.polaris.cache.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisTest.class);

    private static JedisPool jedisPool = null;
    // Redis服务器IP
    private static String ADDR = "192.168.201.100";
    // Redis的端口号
    private static int PORT = 6379;
    // 访问密码
    private static String AUTH = "123456";

    /**
     * 初始化Redis连接池
     */
    static {
        try {
            JedisPoolConfig config = new JedisPoolConfig();
            // 连接耗尽时是否阻塞, false报异常,ture阻塞直到超时, 默认true
            config.setBlockWhenExhausted(true);
            // 设置的逐出策略类名, 默认DefaultEvictionPolicy(当连接超过最大空闲时间,或连接数超过最大空闲连接数)
            config.setEvictionPolicyClassName("org.apache.commons.pool2.impl.DefaultEvictionPolicy");
            // 是否启用pool的jmx管理功能, 默认true
            config.setJmxEnabled(true);
            // 最大空闲连接数, 默认8个 控制一个pool最多有多少个状态为idle(空闲的)的jedis实例。
            config.setMaxIdle(8);
            // 最大连接数, 默认8个
            config.setMaxTotal(200);
            // 表示当borrow(引入)一个jedis实例时，最大的等待时间，如果超过等待时间，则直接抛出JedisConnectionException；
            config.setMaxWaitMillis(1000 * 100);
            // 在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
            config.setTestOnBorrow(true);
            jedisPool = new JedisPool(config, ADDR, PORT, 3000, AUTH);
        } catch (Exception e) {

        }
    }

    /**
     * 获取Jedis实例
     *
     * @return
     */
    public synchronized static Jedis getJedis() {
        try {
            if (jedisPool != null) {
                Jedis resource = jedisPool.getResource();
                return resource;
            } else {
                return null;
            }
        } catch (Exception e) {

            return null;
        }
    }

    /**
     * 释放jedis资源
     *
     * @param jedis
     */
    public static void close(final Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }


    // 插入模拟数据
    @SuppressWarnings("unused")
    private static void insertClusterTest() {
        String key = "testCluster";
        for (int i = 1; i <= 1000; i++) {
            LOGGER.info(RedisUtil.set(key + i, "testClusterValue" + i));
        }
    }

    // 模拟无限循环读取集群测试
    @SuppressWarnings("unused")
    private static void clusterTest() {
        try {
            int num = 1000;
            String key = "testCluster";
            String value = "";
            int count = 1;

            while (true) {
                for (int i = 1; i <= num; i++) {
                    try {
                        // 存数据
                        // jedisCluster.set(key+i, "WuShuicheng"+i);
                        // 取数据
                        value = RedisUtil.get(key + i);
                        LOGGER.info(key + i + "=" + value);
                        if (value == null || "".equals(value)) {
                            LOGGER.info("===>break" + key + i + " value is null");
                            break;
                        }
                    } catch (Exception e) {
                        LOGGER.info("====>" + e);
                        Thread.sleep(3000);
                        continue;
                    }
                    // 删除数据
                    // jedisCluster.del(key+i);
                    // value = jedisCluster.get(key+i);
                }
                LOGGER.info("===================================>count:" + count);
                if (value == null || "".equals(value)) {
                    break;
                }
                count++;
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            LOGGER.info("====>" + e);
        }
    }

    /**
     * 字符串测试
     *
     * @param jedis
     */
    public static void testString(Jedis jedis) {
        jedis.set("name", "xxxx");// 向key-->name中放入了value-->xinxin
        LOGGER.info(jedis.get("name"));// 执行结果：xinxin

        jedis.append("name", " is my lover"); // 拼接
        LOGGER.info(jedis.get("name"));

        jedis.del("name"); // 删除某个键
        LOGGER.info(jedis.get("name"));
        // 设置多个键值对
        jedis.mset("name", "某某某", "age", "24", "qq", "476777XXX");
        jedis.incr("age"); // 进行加1操作
        LOGGER.info(jedis.get("name") + "-" + jedis.get("age") + "-" + jedis.get("qq"));
    }


}