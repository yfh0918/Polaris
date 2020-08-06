package com.polaris.container.gateway.proxy.websocket;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.java_websocket.client.WebSocketClient;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;

public class WsConstant {
    /**
     * 将请求和handshake绑定
     * */
    public static final Map<String, WebSocketServerHandshaker> wsHandshakerMap =
            new ConcurrentHashMap<>();

    /**
     * 将请求和通道绑定
     * */
    public static final Map<String, ChannelHandlerContext> wsCtx =
            new ConcurrentHashMap<>();

    /**
     * 将ctx和handshaker
     * */
    public static final Map<ChannelHandlerContext, WebSocketServerHandshaker> ctxWs =
            new ConcurrentHashMap<>();

    /**
     * 将ctx和handshaker
     * */
    public static final Map<WebSocketClient, ChannelHandlerContext> wsClientCtx =
            new ConcurrentHashMap<>();

    /**
     * 将ctx和handshaker
     * */
    public static final Map<ChannelHandlerContext, WebSocketClient> wsCtxClient =
            new ConcurrentHashMap<>();
}
