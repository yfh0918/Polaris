package com.polaris.container.gateway.proxy.websocket;

public class WebSocketHandlerFactory {
    private static WebSocketHandler handler = new WebSocketHandlerDefaultImpl();
    public static WebSocketHandler get() {
        return handler;
    }
}
