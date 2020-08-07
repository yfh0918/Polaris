package com.polaris.container.gateway.proxy.websocket.client;

import java.lang.reflect.Constructor;

import io.netty.channel.ChannelHandlerContext;

public class WebSocketFactory {

    private static Class<? extends WebSocketAbstract> websocketClientClazz = null;
    
    @SuppressWarnings("rawtypes")
    public static WebSocketInf create(String uri, ChannelHandlerContext ctx) throws Exception {
        if (websocketClientClazz == null) {
            return new WebsocketClientDefault(uri, ctx);
        }
        Class[] paramTypes = { String.class, ChannelHandlerContext.class };
        Object[] params = {uri, ctx}; 
        Constructor con = websocketClientClazz.getConstructor(paramTypes);
        return (WebSocketInf) con.newInstance(params);
    }
    public static void setClientClass(Class<? extends WebSocketAbstract> clientClass) {
        websocketClientClazz = clientClass;
    }
}
