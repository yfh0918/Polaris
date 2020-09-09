package com.polaris.container.gateway.pojo;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.common.net.HostAndPort;
import com.polaris.container.gateway.util.ProxyUtils;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;

public class HttpProtocol {

    private static volatile Map<String, String> connectionMap = new HashMap<>();
    
    private static volatile Map<String, String> tlsMap = new HashMap<>();
    
    private static volatile Map<String, String> http11Map = new HashMap<>();
    
    private static volatile Map<String, String> http20Map = new HashMap<>();
    
    private static volatile Map<String, String> websocketMap = new HashMap<>();

    public static Map<String, String> getConnectionMap() {
        return connectionMap;
    }

    public static void setConnectionMap(Map<String, String> connectionMap) {
        HttpProtocol.connectionMap = connectionMap;
    }

    public static Map<String, String> getTlsMap() {
        return tlsMap;
    }

    public static void setTlsMap(Map<String, String> tlsMap) {
        HttpProtocol.tlsMap = tlsMap;
    }

    public static Map<String, String> getHttp11Map() {
        return http11Map;
    }

    public static void setHttp11Map(Map<String, String> http11Map) {
        HttpProtocol.http11Map = http11Map;
    }

    public static Map<String, String> getHttp20Map() {
        return http20Map;
    }

    public static void setHttp20Map(Map<String, String> http20Map) {
        HttpProtocol.http20Map = http20Map;
    }

    public static Map<String, String> getWebsocketMap() {
        return websocketMap;
    }

    public static void setWebsocketMap(Map<String, String> websocketMap) {
        HttpProtocol.websocketMap = websocketMap;
    }
    
    public static String identifyHostAndPort(HttpRequest httpRequest) {
        String hostAndPort = ProxyUtils.parseHostAndPort(httpRequest);
        if (StringUtils.isBlank(hostAndPort)) {
            List<String> hosts = httpRequest.headers().getAll(
                    HttpHeaderNames.HOST.toString());
            if (hosts != null && !hosts.isEmpty()) {
                hostAndPort = hosts.get(0);
            }
        }
        return hostAndPort;
    }
    
    public static HostAndPort getHostAndPort(String hostAndPort)
            throws UnknownHostException {
        try {
            return HostAndPort.fromString(hostAndPort);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
