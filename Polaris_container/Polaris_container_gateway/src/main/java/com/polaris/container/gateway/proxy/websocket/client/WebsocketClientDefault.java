package com.polaris.container.gateway.proxy.websocket.client;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.enums.ReadyState;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.container.gateway.proxy.websocket.WsAdmin;
import com.polaris.container.gateway.proxy.websocket.WsStatus;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class WebsocketClientDefault extends WebSocketClient implements WebSocketClientInf {
    
    private ChannelHandlerContext ctx;
    
    private static final Logger log = LoggerFactory.getLogger(WebsocketClientDefault.class);
    
    private volatile AtomicBoolean closed = new AtomicBoolean(false);
    
    private volatile AtomicInteger idleConnectTimeout = new AtomicInteger(0);
    
    public WebsocketClientDefault(String uri, ChannelHandlerContext ctx) throws URISyntaxException {
        super(new URI(uri));
        this.ctx = ctx;
    }

    @Override
    public void onOpen(ServerHandshake arg0) {
        log.debug("------ WebsocketClientDefault onOpen ------");
    }

    @Override
    public void onClose(int arg0, String arg1, boolean arg2) {
        if (!closed.compareAndSet(false, true)) {
            return;
        }
        log.debug("------ WebsocketClientDefault onClose ------");
        WsAdmin.close(ctx, new CloseWebSocketFrame());
    }

    @Override
    public void onError(Exception arg0) {
        log.debug("------ WebsocketClientDefault onError ------");
        WsAdmin.close(ctx, new CloseWebSocketFrame());
    }

    @Override
    public void onMessage(ByteBuffer bytes) {
        log.debug("------ WebsocketClientDefault onMessage ByteBuffer ------");
        if (ctx != null && ctx.channel() != null && ctx.channel().isActive()) {
            int len = bytes.limit() - bytes.position();
            byte[] newBytes = new byte[len];
            bytes.get(newBytes);
            resetIdleConnectTimeout();
            ctx.writeAndFlush(new BinaryWebSocketFrame(io.netty.buffer.Unpooled.copiedBuffer(newBytes)));
        } else {
            WsAdmin.close(ctx, new CloseWebSocketFrame());
        }
    }
    
    @Override
    public void onMessage(String message) {
        log.debug("------ WebsocketClientDefault onMessage text ------");
        if (ctx != null && ctx.channel() != null && ctx.channel().isActive()) {
            resetIdleConnectTimeout();
            ctx.writeAndFlush(new TextWebSocketFrame(message));
        } else {
            WsAdmin.close(ctx, new CloseWebSocketFrame());
        }
    }

    @Override
    public WsStatus getState() {
        if (getReadyState().equals(ReadyState.NOT_YET_CONNECTED)) {
            return WsStatus.NOT_YET_CONNECTED;
        } else if (getReadyState().equals(ReadyState.OPEN)) {
            return WsStatus.OPEN;
        } else if (getReadyState().equals(ReadyState.CLOSING)) {
            return WsStatus.CLOSING;
        } else if (getReadyState().equals(ReadyState.CLOSED)) {
            return WsStatus.CLOSED;
        } 
        return WsStatus.NOT_YET_CONNECTED;
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