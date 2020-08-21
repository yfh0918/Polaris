package com.polaris.container.gateway.proxy.websocket;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.polaris.container.gateway.proxy.websocket.client.WebSocketClient;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;

public class WsAdmin {

    /**
     * 将ctx关联
     * */
    private static final Map<ChannelHandlerContext, WsAdmin> contextMap =
            new ConcurrentHashMap<>();

    public static WsAdmin get(ChannelHandlerContext context) {
        return contextMap.get(context);
    }
    public static void close() {
        for (Map.Entry<ChannelHandlerContext, WsAdmin> entry : contextMap.entrySet()) {
            close0(entry.getKey(),entry.getValue());
        }
        contextMap.clear();
    }
    public static void close(ChannelHandlerContext context) {
        WsAdmin wsAdmin = contextMap.remove(context);
        if (wsAdmin == null) {
            return;
        }
        close0(context, wsAdmin);
    }
    private static void close0(ChannelHandlerContext context, WsAdmin wsAdmin) {
        wsAdmin.getWebSocketServerHandshaker().close(context.channel(), new CloseWebSocketFrame());
        wsAdmin.getWebSocketClient().close();
    }
    public static int size() {
        return contextMap.size();
    }
    private String uri;
    
    private WebSocketServerHandshaker webSocketServerHandshaker;
    
    private ChannelHandlerContext channelHandlerContext;
    
    private WebSocketClient webSocketClient;

    public String getUri() {
        return uri;
    }

    public WsAdmin setUri(String uri) {
        this.uri = uri;
        return this;
    }

    public WebSocketServerHandshaker getWebSocketServerHandshaker() {
        return webSocketServerHandshaker;
    }

    public WsAdmin setWebSocketServerHandshaker(WebSocketServerHandshaker webSocketServerHandshaker) {
        this.webSocketServerHandshaker = webSocketServerHandshaker;
        return this;
    }

    public ChannelHandlerContext getChannelHandlerContext() {
        return channelHandlerContext;
    }

    public WsAdmin setChannelHandlerContext(ChannelHandlerContext channelHandlerContext) {
        this.channelHandlerContext = channelHandlerContext;
        contextMap.put(channelHandlerContext, this);
        return this;
    }

    public WebSocketClient getWebSocketClient() {
        return webSocketClient;
    }

    public WsAdmin setWebSocketClient(WebSocketClient webSocketClient) {
        this.webSocketClient = webSocketClient;
        return this;
    }
}
