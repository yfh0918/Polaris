package com.polaris.container.gateway;

public interface HttpFilterChain <T extends HttpFilter>{
    void add(T filter);
    void remove(T filter);
}
