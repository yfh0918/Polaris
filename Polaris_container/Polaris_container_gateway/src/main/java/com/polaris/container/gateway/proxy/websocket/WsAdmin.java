package com.polaris.container.gateway.proxy.websocket;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.polaris.container.gateway.proxy.websocket.client.WebSocketClientInf;

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
    public static void close(ChannelHandlerContext context,CloseWebSocketFrame frame) {
        WsAdmin ws = contextMap.remove(context);
        if (ws == null) {
            return;
        }
        if (frame == null) {
            frame = new CloseWebSocketFrame();
        }
        ws.getWebSocketServerHandshaker().close(context.channel(), frame);
        ws.getWebSocketClient().close();
    }
    public static int size() {
        return contextMap.size();
    }
    private String uri;
    
    private WebSocketServerHandshaker webSocketServerHandshaker;
    
    private ChannelHandlerContext channelHandlerContext;
    
    private WebSocketClientInf webSocketClient;

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

    public WebSocketClientInf getWebSocketClient() {
        return webSocketClient;
    }

    public WsAdmin setWebSocketClient(WebSocketClientInf webSocketClient) {
        this.webSocketClient = webSocketClient;
        return this;
    }

}
