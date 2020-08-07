package com.polaris.container.gateway.proxy.websocket.client;

import java.lang.reflect.Constructor;

import io.netty.channel.ChannelHandlerContext;

public class WebSocketClientFactory {

    private static Class<? extends WebSocketClientInf> websocketClientClazz = WebsocketClientDefault.class;
    
    @SuppressWarnings("rawtypes")
    public static WebSocketClientInf create(String uri, ChannelHandlerContext ctx) throws Exception {
        Class[] paramTypes = { String.class, ChannelHandlerContext.class };
        Object[] params = {uri, ctx}; 
        Constructor con = websocketClientClazz.getConstructor(paramTypes);
        return (WebSocketClientInf) con.newInstance(params);
    }
    public static void setClientClass(Class<? extends WebSocketClientInf> clientClass) {
        websocketClientClazz = clientClass;
    }
}
