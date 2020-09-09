package com.polaris.container.gateway.pojo;

import java.util.ArrayList;
import java.util.List;

import io.netty.handler.codec.http.HttpMethod;

public class HttpCors {
    private static boolean enable = false;

    private static boolean allowCredentials = false;
    private static long maxAge = 0l;
    private static List<String> allowOrigin = new ArrayList<>();
    private static List<String> allowHeaders = new ArrayList<>();
    private static List<HttpMethod> allowMethods = new ArrayList<>();
    private static List<String> exposeHeaders = new ArrayList<>();
    public static boolean isEnable() {
        return enable;
    }
    public static void setEnable(boolean enable) {
        HttpCors.enable = enable;
    }
    public static boolean allowCredentials() {
        return allowCredentials;
    }
    public static void setAllowCredentials(boolean allowCredentials) {
        HttpCors.allowCredentials = allowCredentials;
    }
    public static long getMaxAge() {
        return maxAge;
    }
    public static void setMaxAge(long maxAge) {
        HttpCors.maxAge = maxAge;
    }
    public static List<String> getAllowOrigin() {
        return allowOrigin;
    }
    public static void setAllowOrigin(List<String> allowOrigin) {
        HttpCors.allowOrigin = allowOrigin;
    }
    public static List<String> getAllowHeaders() {
        return allowHeaders;
    }
    public static void setAllowHeaders(List<String> allowHeaders) {
        HttpCors.allowHeaders = allowHeaders;
    }
    public static List<HttpMethod> getAllowMethods() {
        return allowMethods;
    }
    public static void setAllowMethods(List<HttpMethod> allowMethods) {
        HttpCors.allowMethods = allowMethods;
    }
    public static List<String> getExposeHeaders() {
        return exposeHeaders;
    }
    public static void setExposeHeaders(List<String> exposeHeaders) {
        HttpCors.exposeHeaders = exposeHeaders;
    }

}
