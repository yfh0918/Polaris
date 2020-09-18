package com.polaris.container.gateway.proxy.websocket.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger log = LoggerFactory.getLogger(WebSocketNettyClientHandler.class);
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
        log.debug("channelInactive trigger onClose");
        clientListener.onClose(null);
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
            clientListener.onClose((CloseWebSocketFrame)frame);
        }
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("exceptionCaught trigger onClose",cause);
        clientListener.onClose(null);
    }
}