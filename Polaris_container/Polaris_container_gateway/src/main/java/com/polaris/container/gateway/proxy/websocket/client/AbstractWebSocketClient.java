package com.polaris.container.gateway.proxy.websocket.client;

import java.net.URISyntaxException;
import java.util.concurrent.atomic.AtomicInteger;

import io.netty.channel.ChannelHandlerContext;

public abstract class AbstractWebSocketClient implements WebSocketClientInf {
    
    protected String uri;
    protected ChannelHandlerContext ctx;
    private volatile AtomicInteger idleConnectTimeout = new AtomicInteger(0);

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
    
    @Override
    public int addAndGetIdleConnectTimeout(int idleConnectTimeout) {
        return this.idleConnectTimeout.addAndGet(idleConnectTimeout);
    }

    @Override
    public void resetIdleConnectTimeout() {
        this.idleConnectTimeout.set(0);
    }
}
