package com.polaris.container.gateway.proxy.websocket;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.polaris.container.gateway.proxy.websocket.client.WebSocketInf;

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
        ws.getWebSocketClient().close();
        ws.getWebSocketServerHandshaker().close(context.channel(), frame);
    }
    public static int size() {
        return contextMap.size();
    }
    private String uri;
    
    private WebSocketServerHandshaker webSocketServerHandshaker;
    
    private ChannelHandlerContext channelHandlerContext;
    
    private WebSocketInf webSocketClient;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public WebSocketServerHandshaker getWebSocketServerHandshaker() {
        return webSocketServerHandshaker;
    }

    public void setWebSocketServerHandshaker(WebSocketServerHandshaker webSocketServerHandshaker) {
        this.webSocketServerHandshaker = webSocketServerHandshaker;
    }

    public ChannelHandlerContext getChannelHandlerContext() {
        return channelHandlerContext;
    }

    public void setChannelHandlerContext(ChannelHandlerContext channelHandlerContext) {
        this.channelHandlerContext = channelHandlerContext;
        contextMap.put(channelHandlerContext, this);
    }

    public WebSocketInf getWebSocketClient() {
        return webSocketClient;
    }

    public void setWebSocketClient(WebSocketInf webSocketClient) {
        this.webSocketClient = webSocketClient;
    }

}
