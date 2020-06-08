package com.polaris.core.util;

import java.util.UUID;

/**
 * 操作唯一性util
 *
 * @author yufenghua
 */
public abstract class UuidUtil {

    /**
     * 生成uuid
     *
     * @return
     */
    public static String generateUuid() {
        return UUID.randomUUID().toString();
    }

}
