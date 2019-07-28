package com.polaris.core.util;

import java.util.UUID;

import com.polaris.core.config.ConfClient;

/**
 * 操作唯一性util(避免服务器重复请求)
 *
 * @author yufenghua
 */
public class UuidUtil {

    //幂等性Key
    public static final String IDEMPOTENCY = "idempotency";
    private static volatile SnowflakeIdWorker idWorker;

    private UuidUtil() {
    }
    
    static {
    	idWorker = new SnowflakeIdWorker(ConfClient.getUuidWorkId(), ConfClient.getUuidDatacenterId());
    }

    /**
     * 生成uuid
     *
     * @return
     */
    public static String generateUuid() {
        return UUID.randomUUID().toString();
    }

    /**
     * 生成uuid，主要用于数据库的自增ID
     *
     * @return
     */
    public static long generateLongUuid() {
    	return idWorker.nextId();
    }

}
