package com.polaris.container.gateway.proxy.websocket.client;

import java.lang.reflect.Constructor;

import com.polaris.container.gateway.proxy.websocket.client.netty.WebSocketNettyClient;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;

public class WebSocketClientFactory {

    private static Class<? extends WebSocketClient> websocketClientClazz = WebSocketNettyClient.class;
    
    @SuppressWarnings("rawtypes")
    public static WebSocketClient create(String uri, EventLoopGroup eventLoopGroup, ChannelHandlerContext ctx) throws Exception {
        Class[] paramTypes = { String.class, EventLoopGroup.class, ChannelHandlerContext.class };
        Object[] params = {uri, eventLoopGroup, ctx}; 
        Constructor con = websocketClientClazz.getConstructor(paramTypes);
        return (WebSocketClient) con.newInstance(params);
    }
    public static void setClientClass(Class<? extends WebSocketClient> clientClass) {
        websocketClientClazz = clientClass;
    }
}
