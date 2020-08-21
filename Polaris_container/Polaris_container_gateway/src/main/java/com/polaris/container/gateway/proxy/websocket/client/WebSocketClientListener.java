package com.polaris.container.gateway.proxy.websocket.client;

import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public interface WebSocketClientListener {
    void onPing(PingWebSocketFrame frame);
    void onPong(PongWebSocketFrame frame);
    void onMessage(TextWebSocketFrame frame);
    void onMessage(BinaryWebSocketFrame frame);
    void onClose(CloseWebSocketFrame frame);
}
