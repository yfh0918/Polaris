package com.polaris.container.gateway.proxy.websocket;

import com.polaris.container.gateway.pojo.HttpRequestWrapper;
import com.polaris.container.gateway.proxy.HostResolver;
import com.polaris.container.gateway.proxy.HttpFilters;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

public interface WebSocketHandler {
    boolean isWSProtocol(HttpRequestWrapper req);
    
    void upgrade(HttpRequestWrapper req, 
                 ChannelHandlerContext ctx, 
                 HostResolver hostResolver, 
                 EventLoopGroup eventLoopGroup,
                 HttpFilters filters);
    
    void proxyToServer(ChannelHandlerContext ctx, WebSocketFrame frame);
    
    boolean userEventTriggered(ChannelHandlerContext ctx,int idleConnectTimeout);
}
