package com.polaris.core.util;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.apache.commons.lang3.StringUtils;

/**
 * @author 
 */
public final class ByteUtil {

    public static final byte[] EMPTY = new byte[0];

    public static byte[] toBytes(String s) {
        if (s == null) {
            return EMPTY;
        }
        return s.getBytes(Charset.forName(StandardCharsets.UTF_8.name()));
    }

    public static byte[] toBytes(Object s) {
        if (s == null) {
            return EMPTY;
        }
        return toBytes(String.valueOf(s));
    }

    public static String toString(byte[] bytes) {
        if (bytes == null) {
            return StringUtils.EMPTY;
        }
        return new String(bytes, Charset.forName(StandardCharsets.UTF_8.name()));
    }

    public static boolean isEmpty(byte[] data) {
        return data == null || data.length == 0;
    }

    public static boolean isNotEmpty(byte[] data) {
        return !isEmpty(data);
    }

}
