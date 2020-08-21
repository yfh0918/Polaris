package com.polaris.container.gateway.proxy.websocket.client.java_websocket;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.enums.ReadyState;
import org.java_websocket.framing.Framedata;
import org.java_websocket.framing.PingFrame;
import org.java_websocket.framing.PongFrame;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.container.gateway.proxy.websocket.WsAdmin;
import com.polaris.container.gateway.proxy.websocket.client.WebSocketStatus;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class WebsocketClientJavaWs extends WebSocketClient implements com.polaris.container.gateway.proxy.websocket.client.WebSocketClient {
    
    private ChannelHandlerContext ctx;
    
    private static final Logger log = LoggerFactory.getLogger(WebsocketClientJavaWs.class);
    
    private volatile AtomicBoolean closed = new AtomicBoolean(false);
    
    private volatile AtomicInteger idleConnectTimeout = new AtomicInteger(0);
    
    public WebsocketClientJavaWs(String uri, ChannelHandlerContext ctx) throws URISyntaxException {
        super(new URI(uri));
        this.ctx = ctx;
    }

    @Override
    public void onOpen(ServerHandshake arg0) {
        log.debug("------ WebsocketClientDefault onOpen ------");
    }

    @Override
    public void onWebsocketPing( WebSocket conn, Framedata f ) {
        if (ctx != null && ctx.channel() != null && ctx.channel().isActive()) {
            ctx.writeAndFlush((PingFrame)f);
            resetIdleConnectTimeout();
        } else {
            WsAdmin.close(ctx);
        }
    }

    /**
     * This default implementation does not do anything. Go ahead and overwrite it.
     *
     * @see org.java_websocket.WebSocketListener#onWebsocketPong(WebSocket, Framedata)
     */
    @Override
    public void onWebsocketPong( WebSocket conn, Framedata f ) {
        if (ctx != null && ctx.channel() != null && ctx.channel().isActive()) {
            ctx.writeAndFlush((PongFrame)f);
            resetIdleConnectTimeout();
        } else {
            WsAdmin.close(ctx);
        }
    }
    
    @Override
    public void onClose(int arg0, String arg1, boolean arg2) {
        if (!closed.compareAndSet(false, true)) {
            return;
        }
        log.debug("------ WebsocketClientDefault onClose ------");
        WsAdmin.close(ctx);
    }

    @Override
    public void onError(Exception arg0) {
        log.debug("------ WebsocketClientDefault onError ------");
        WsAdmin.close(ctx);
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
            WsAdmin.close(ctx);
        }
    }
    
    @Override
    public void onMessage(String message) {
        log.debug("------ WebsocketClientDefault onMessage text ------");
        if (ctx != null && ctx.channel() != null && ctx.channel().isActive()) {
            resetIdleConnectTimeout();
            ctx.writeAndFlush(new TextWebSocketFrame(message));
        } else {
            WsAdmin.close(ctx);
        }
    }

    @Override
    public WebSocketStatus getState() {
        if (getReadyState().equals(ReadyState.NOT_YET_CONNECTED)) {
            return WebSocketStatus.NOT_YET_CONNECTED;
        } else if (getReadyState().equals(ReadyState.OPEN)) {
            return WebSocketStatus.OPEN;
        } else if (getReadyState().equals(ReadyState.CLOSING)) {
            return WebSocketStatus.CLOSING;
        } else if (getReadyState().equals(ReadyState.CLOSED)) {
            return WebSocketStatus.CLOSED;
        } 
        return WebSocketStatus.NOT_YET_CONNECTED;
    }
    
    @Override
    public int addAndGetIdleConnectTimeout(int idleConnectTimeout) {
        return this.idleConnectTimeout.addAndGet(idleConnectTimeout);
    }

    @Override
    public void resetIdleConnectTimeout() {
        this.idleConnectTimeout.set(0);
    }

    @Override
    public void sendPing(PingWebSocketFrame frame) {
        resetIdleConnectTimeout();
        super.sendPing();
    }

    @Override
    public void sendPong(PongWebSocketFrame frame) {
        resetIdleConnectTimeout();
    }

    @Override
    public void send(BinaryWebSocketFrame frame) {
        resetIdleConnectTimeout();
        ByteBuf content = frame.content();
        final int length = content.readableBytes();
        final byte[] array = new byte[length];
        content.getBytes(content.readerIndex(), array, 0, length);
        send(ByteBuffer.wrap(array));
    }

    @Override
    public void send(TextWebSocketFrame frame) {
        resetIdleConnectTimeout();
        send(frame.text());
    }
}