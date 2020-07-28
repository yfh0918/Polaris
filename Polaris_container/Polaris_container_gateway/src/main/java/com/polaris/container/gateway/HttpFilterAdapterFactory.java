package com.polaris.container.gateway;

import com.polaris.container.gateway.proxy.HttpFilters;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;

public class HttpFilterAdapterFactory {

    private static HttpFilters adapater;
    public synchronized static HttpFilters get(HttpRequest originalRequest, ChannelHandlerContext ctx) {
        if (adapater == null) {
            adapater = new HttpFilterAdapterImpl(originalRequest,ctx);
        }
        return adapater;
    }
}
