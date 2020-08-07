package com.polaris.container.gateway.proxy.websocket.client;

import java.net.URISyntaxException;

import io.netty.channel.ChannelHandlerContext;

public abstract class AbstractWebSocketClient implements WebSocketClientInf {
    
    protected String uri;
    protected ChannelHandlerContext ctx;
    
    /**
     * Constructs a WebSocketClient instance and sets it to the connect to the
     * specified URI. The channel does not attampt to connect automatically. The connection
     * will be established once you call <var>connect</var>.
     *
     * @param serverUri the server URI to connect to
     */
    public AbstractWebSocketClient(String uri, ChannelHandlerContext ctx) throws URISyntaxException {
        this.ctx = ctx;
        this.uri = uri;
    }
}
