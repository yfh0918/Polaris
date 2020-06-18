package com.polaris.core.util;

public class SystemClock {
    public static long now() {
        return cn.hutool.core.date.SystemClock.now();
    }
    public static String nowDate() {
        return cn.hutool.core.date.SystemClock.nowDate();
    }
}
