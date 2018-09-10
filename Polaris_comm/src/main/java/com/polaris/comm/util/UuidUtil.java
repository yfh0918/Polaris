package com.polaris.comm.util;

import java.util.UUID;

import com.polaris.comm.config.ConfClient;

/**
 * 操作唯一性util(避免服务器重复请求)
 *
 * @author yufenghua
 */
public class UuidUtil {

    //幂等性Key
    public static final String IDEMPOTENCY = "idempotency";

    private UuidUtil() {
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
    	long workerId = Long.parseLong(ConfClient.get("uuid.wokerId", "0", false)); 
    	long datacenterId = Long.parseLong(ConfClient.get("uuid.datacenterId", "0", false)); 
    	SnowflakeIdWorker idWorker = new SnowflakeIdWorker(workerId, datacenterId);
    	return idWorker.nextId();
    }

}
