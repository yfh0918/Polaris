package com.polaris.container.gateway.proxy.jersey;

import com.polaris.container.gateway.HttpFilter;
import com.polaris.container.gateway.pojo.HttpFilterMessage;
import com.polaris.core.component.LifeCycle;

import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;

public abstract class JerseyFilter extends HttpFilter<HttpRequest>{

    @Override
    public HttpFilterMessage doFilter(HttpRequest httpMessage, HttpObject httpObject) {
        return null;
    }
    
    /**
     * 构造函数并加入调用链
     *
     */
    @Override
    public void starting(LifeCycle event) {
        super.starting(event);
        JerseyConfig.add(this);
    } 
}
