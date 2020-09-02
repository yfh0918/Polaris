package com.polaris.container.gateway;

import com.polaris.container.gateway.proxy.HttpFilters;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;

public class HttpFilterAdapterFactory {

    public synchronized static HttpFilters get(HttpRequest originalRequest, ChannelHandlerContext ctx) {
        return new HttpFilterAdapterImpl(originalRequest,ctx);
    }
}
