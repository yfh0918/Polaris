package com.polaris.extension.cache.redis;

import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class RedissonLockUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedissonLockUtil.class);


    /**
     * 获取默认锁
     */
    public static RLock acquireLock(RedissonClient redissonClient, String lockName) {
        Redisson redisson = (Redisson) redissonClient;
        RLock fairLock = redisson.getLock(lockName);
        try {
            fairLock.lock();// 手动去解锁
            LOGGER.error("锁 - {}获取成功", lockName);
        } catch (Exception e) {
            LOGGER.error("加锁失败 - {}", lockName);
            LOGGER.error(e.getMessage(), e);
        }

        return fairLock;
    }

    /**
     * 释放默认锁
     */
    public static void realeaseLock(RLock fairLock) {
        fairLock.unlock();
    }


}
