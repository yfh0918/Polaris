package com.polaris.container.gateway.proxy.websocket.client.netty;

import java.util.concurrent.atomic.AtomicBoolean;

import com.polaris.container.gateway.proxy.websocket.client.WebSocketClientListener;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketHandshakeException;

public class WebSocketNettyClientHandler extends SimpleChannelInboundHandler<Object> {
    private final WebSocketClientHandshaker handshaker;
    private ChannelPromise handshakeFuture;
    private WebSocketClientListener clientListener;
    private volatile AtomicBoolean closed = new AtomicBoolean(false);

    public WebSocketNettyClientHandler(WebSocketClientHandshaker handshaker, WebSocketClientListener clientListener) {
        this.handshaker = handshaker;
        this.clientListener = clientListener;
    }

    public ChannelFuture handshakeFuture() {
        return handshakeFuture;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        handshakeFuture = ctx.newPromise();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        handshaker.handshake(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        close(ctx);
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel ch = ctx.channel();
        if (!handshaker.isHandshakeComplete()) {
            try {
                handshaker.finishHandshake(ch, (FullHttpResponse) msg);
                handshakeFuture.setSuccess();
            } catch (WebSocketHandshakeException e) {
                handshakeFuture.setFailure(e);
            }
            return;
        }

        WebSocketFrame frame = (WebSocketFrame) msg;
        if (frame instanceof TextWebSocketFrame) {
            clientListener.onMessage((TextWebSocketFrame)frame);
        } else if (frame instanceof BinaryWebSocketFrame) {
            clientListener.onMessage((BinaryWebSocketFrame)frame);
        } else if (frame instanceof PongWebSocketFrame) {
            clientListener.onPong((PongWebSocketFrame)frame);
        } else if (frame instanceof PingWebSocketFrame) {
            clientListener.onPing((PingWebSocketFrame)frame);
        } else if (frame instanceof CloseWebSocketFrame) {
            close(ctx);
        }
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        close(ctx);
    }
    
    private void close(ChannelHandlerContext ctx) {
        if (!closed.compareAndSet(false, true)) {
            return;
        }
        ctx.close();
        clientListener.onClose(null);
    }
}