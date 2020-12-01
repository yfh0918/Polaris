package com.polaris.container.gateway.pojo;

import java.util.LinkedHashMap;
import java.util.Map;

public class HttpUpstream {
    
    private volatile static Map<String, HttpUpstream> upstreamMap = new LinkedHashMap<>();

    public static Map<String, HttpUpstream> getUpstreamMap() {
        return upstreamMap;
    }
    public static void setUpstreamMap(Map<String, HttpUpstream> upstreamMap) {
        HttpUpstream.upstreamMap = upstreamMap;
    }
    private String name;
    private String host;
    public String getName() {
        return name;
    }
    public String getHost() {
        return host;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setHost(String host) {
        this.host = host;
    }
}
