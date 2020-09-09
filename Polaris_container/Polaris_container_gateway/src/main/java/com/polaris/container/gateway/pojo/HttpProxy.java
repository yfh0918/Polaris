package com.polaris.container.gateway.pojo;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HttpProxy {

    //html
    private volatile static Map<String, List<HttpProxy>> serverNameMap = new LinkedHashMap<>();
    private volatile static Map<String, HttpProxy> serverNameContextMap = new LinkedHashMap<>();
    
    public static Map<String, List<HttpProxy>> getServerNameMap() {
        return serverNameMap;
    }
    public static void setServerNameMap(Map<String, List<HttpProxy>> serverNameMap) {
        HttpProxy.serverNameMap = serverNameMap;
    }
    public static Map<String, HttpProxy> getServerNameContextMap() {
        return serverNameContextMap;
    }
    public static void setServerNameContextMap(Map<String, HttpProxy> serverNameContextMap) {
        HttpProxy.serverNameContextMap = serverNameContextMap;
    }
    private String name;
    private String context;
    private String index;
    private String root;
    private String proxy;
    private String rewrite;
    private String notFound;
    private String error;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getContext() {
        return context;
    }
    public void setContext(String context) {
        this.context = context;
    }
    public String getIndex() {
        return index;
    }
    public void setIndex(String index) {
        this.index = index;
    }
    public String getRoot() {
        return root;
    }
    public void setRoot(String root) {
        this.root = root;
    }
    public String getProxy() {
        return proxy;
    }
    public void setProxy(String proxy) {
        this.proxy = proxy;
    }
    public String getRewrite() {
        return rewrite;
    }
    public void setRewrite(String rewrite) {
        this.rewrite = rewrite;
    }
    public String getNotFound() {
        return notFound;
    }
    public void setNotFound(String notFound) {
        this.notFound = notFound;
    }
    public String getError() {
        return error;
    }
    public void setError(String error) {
        this.error = error;
    }

    
}
