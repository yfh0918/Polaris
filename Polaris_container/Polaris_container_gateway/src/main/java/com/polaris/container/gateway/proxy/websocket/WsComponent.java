package com.polaris.container.gateway.proxy.websocket;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.java_websocket.client.WebSocketClient;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;

public class WsComponent {

    /**
     * 将ctx关联
     * */
    private static final Map<ChannelHandlerContext, WsComponent> contextMap =
            new ConcurrentHashMap<>();

    public static WsComponent get(ChannelHandlerContext context) {
        return contextMap.get(context);
    }
    public static void close(ChannelHandlerContext context,CloseWebSocketFrame frame) {
        WsComponent ws = contextMap.get(context);
        if (ws == null) {
            return;
        }
        ws.getWebSocketClient().close();
        ws.getWebSocketServerHandshaker().close(context.channel(), frame);
        contextMap.remove(context);
    }
    
    private String uri;
    
    private WebSocketServerHandshaker webSocketServerHandshaker;
    
    private ChannelHandlerContext channelHandlerContext;
    
    private WebSocketClient webSocketClient;

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

    public WebSocketClient getWebSocketClient() {
        return webSocketClient;
    }

    public void setWebSocketClient(WebSocketClient webSocketClient) {
        this.webSocketClient = webSocketClient;
    }
}
