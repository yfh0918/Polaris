package com.polaris.container.gateway.proxy.websocket;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.polaris.container.gateway.proxy.websocket.client.WebSocketClient;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;

public class WebSocketAdmin {

    /**
     * 将ctx关联
     * */
    private static final Map<ChannelHandlerContext, WebSocketAdmin> contextMap =
            new ConcurrentHashMap<>();

    public static WebSocketAdmin get(ChannelHandlerContext context) {
        return contextMap.get(context);
    }
    public static void close(ChannelHandlerContext context) {
        WebSocketAdmin wsAdmin = contextMap.remove(context);
        if (wsAdmin == null) {
            return;
        }
        close0(context, wsAdmin);
    }
    private static void close0(ChannelHandlerContext context, WebSocketAdmin wsAdmin) {
        wsAdmin.getWebSocketServerHandshaker().close(context.channel(), new CloseWebSocketFrame());
        wsAdmin.getWebSocketClient().close();
    }
    public static int size() {
        return contextMap.size();
    }
    private WebSocketServerHandshaker webSocketServerHandshaker;
    
    private ChannelHandlerContext channelHandlerContext;
    
    private WebSocketClient webSocketClient;
    
    public WebSocketServerHandshaker getWebSocketServerHandshaker() {
        return webSocketServerHandshaker;
    }

    public WebSocketAdmin setWebSocketServerHandshaker(WebSocketServerHandshaker webSocketServerHandshaker) {
        this.webSocketServerHandshaker = webSocketServerHandshaker;
        return this;
    }

    public ChannelHandlerContext getChannelHandlerContext() {
        return channelHandlerContext;
    }

    public WebSocketAdmin setChannelHandlerContext(ChannelHandlerContext channelHandlerContext) {
        this.channelHandlerContext = channelHandlerContext;
        contextMap.put(channelHandlerContext, this);
        return this;
    }

    public WebSocketClient getWebSocketClient() {
        return webSocketClient;
    }

    public WebSocketAdmin setWebSocketClient(WebSocketClient webSocketClient) {
        this.webSocketClient = webSocketClient;
        return this;
    }
}
