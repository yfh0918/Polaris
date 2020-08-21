package com.polaris.container.gateway.proxy.websocket.client.netty;
import java.net.URISyntaxException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.container.gateway.proxy.websocket.WsAdmin;
import com.polaris.container.gateway.proxy.websocket.client.AbstractWebSocketClient;
import com.polaris.container.gateway.proxy.websocket.client.WebSocketStatus;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
public class WebSocketNettyClient extends AbstractWebSocketClient{
    private static final Logger log = LoggerFactory.getLogger(WebSocketNettyClientHandler.class);
    private WebSocketStatus status = WebSocketStatus.NOT_YET_CONNECTED;
    private WebSocketNettyConnect connect;
    
    private volatile AtomicBoolean closed = new AtomicBoolean(false);

    public WebSocketNettyClient(String uri, ChannelHandlerContext ctx) throws URISyntaxException {
        super(uri, ctx);
    }
    
    @Override
    public void connect() {
        connect = WebSocketNettyConnect.getConnect(uri, this);
        status = WebSocketStatus.OPEN;
    }

    @Override
    public void close() {
        close0();
    }

    @Override
    public void sendPing(PingWebSocketFrame frame) {
        super.sendPing(frame);
        connect.getChannelClient().writeAndFlush(frame);
    }

    @Override
    public void send(BinaryWebSocketFrame frame) {
        super.send(frame);
        connect.getChannelClient().writeAndFlush(frame);
    }

    @Override
    public void send(TextWebSocketFrame frame) {
        super.send(frame);
        connect.getChannelClient().writeAndFlush(frame);
    }

    @Override
    public WebSocketStatus getState() {
        return status;
    }

    @Override
    public void sendPong(PongWebSocketFrame frame) {
        super.sendPong(frame);
        connect.getChannelClient().writeAndFlush(frame);
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
        close0();
    }
    
    public void onResponse(WebSocketFrame frame) {
        if (ctx != null && ctx.channel() != null && ctx.channel().isActive()) {
            ctx.writeAndFlush(frame.retain());
        } else {
            close0();
        }
    }
    
    private void close0() {
        if (!closed.compareAndSet(false, true)) {
            return;
        }
        log.debug("------WebSocketClientNetty CloseWebSocketFrame ------");
        WsAdmin.close(ctx);
        connect.getChannelClient().close();
    }
}
