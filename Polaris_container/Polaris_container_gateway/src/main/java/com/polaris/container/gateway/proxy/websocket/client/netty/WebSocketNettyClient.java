package com.polaris.container.gateway.proxy.websocket.client.netty;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.container.gateway.proxy.websocket.WebSocketAdmin;
import com.polaris.container.gateway.proxy.websocket.WebSocketStatus;
import com.polaris.container.gateway.proxy.websocket.client.AbstractWebSocketClient;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
public class WebSocketNettyClient extends AbstractWebSocketClient{
    private static final Logger log = LoggerFactory.getLogger(WebSocketNettyClientHandler.class);
    private WebSocketStatus status = WebSocketStatus.NOT_YET_CONNECTED;
    private Channel channel;
    
    public WebSocketNettyClient(String uri, EventLoopGroup eventLoopGroup, ChannelHandlerContext ctx) throws URISyntaxException {
        super(uri, eventLoopGroup, ctx);
    }
    
    @Override
    public void connect() {
        channel = WebSocketNettyConnector.getChannel(uri,eventLoopGroup, this);
        status = WebSocketStatus.OPEN;
    }

    @Override
    public void close() {
        channel.close();
    }

    @Override
    public void sendPing(PingWebSocketFrame frame) {
        super.sendPing(frame);
        channel.writeAndFlush(frame);
    }

    @Override
    public void send(BinaryWebSocketFrame frame) {
        super.send(frame);
        channel.writeAndFlush(frame);
    }

    @Override
    public void send(TextWebSocketFrame frame) {
        super.send(frame);
        channel.writeAndFlush(frame);
    }

    @Override
    public WebSocketStatus getState() {
        return status;
    }

    @Override
    public void sendPong(PongWebSocketFrame frame) {
        super.sendPong(frame);
        channel.writeAndFlush(frame);
    }
    @Override
    public void onPing(PingWebSocketFrame frame) {
        log.debug("------ WebSocketClientNetty onPing ------");
        super.onPing(frame);
        onResponse(frame);
    }
    @Override
    public void onPong(PongWebSocketFrame frame) {
        log.debug("------ WebSocketClientNetty onPong ------");
        super.onPong(frame);
        onResponse(frame);
    }
    @Override
    public void onMessage(TextWebSocketFrame frame) {
        log.debug("------ WebSocketClientNetty onMessage text ------");
        super.onMessage(frame);
        onResponse(frame);
    }
    @Override
    public void onMessage(BinaryWebSocketFrame frame) {
        log.debug("------ WebSocketClientNetty onMessage byte ------");
        super.onMessage(frame);
        onResponse(frame);
    }
    @Override
    public void onClose(CloseWebSocketFrame frame) {
        log.debug("------WebSocketClientNetty CloseWebSocketFrame ------");
        WebSocketAdmin.close(ctx);
    }
    
    public void onResponse(WebSocketFrame frame) {
        if (ctx != null && ctx.channel() != null && ctx.channel().isActive()) {
            ctx.writeAndFlush(frame.retain());
        } else {
            WebSocketAdmin.close(ctx);
        }
    }
}
