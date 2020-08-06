package com.polaris.container.gateway.proxy.websocket;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class WebsocketClientImpl extends WebSocketClient {
    private ChannelHandlerContext ctx;
    
    private static final Logger log = LoggerFactory.getLogger(WebsocketClientImpl.class);
    
    public WebsocketClientImpl(String uri, ChannelHandlerContext ctx) throws URISyntaxException {
        super(new URI(uri));
        this.ctx = ctx;
    }

    @Override
    public void onOpen(ServerHandshake arg0) {
        log.debug("------ WebsocketClientImpl onOpen ------");
    }

    @Override
    public void onClose(int arg0, String arg1, boolean arg2) {
        log.debug("------ WebsocketClientImpl onClose ------");
    }

    @Override
    public void onError(Exception arg0) {
        log.debug("------ WebsocketClientImpl onError ------");
    }

    @Override
    public void onMessage(ByteBuffer bytes) {
        log.debug("------ WebsocketClientImpl onMessage ByteBuffer ------");
        if (ctx != null && ctx.channel() != null && ctx.channel().isActive()) {
            int len = bytes.limit() - bytes.position();
            byte[] newBytes = new byte[len];
            bytes.get(newBytes);
            ctx.writeAndFlush(new BinaryWebSocketFrame(io.netty.buffer.Unpooled.copiedBuffer(newBytes)));
        } else {
            WsConstant.ctxWs.get(ctx).close(ctx.channel(), new CloseWebSocketFrame());
        }
    }
    
    @Override
    public void onMessage(String message) {
        log.debug("------ WebsocketClientImpl onMessage text ------");
        if (ctx != null && ctx.channel() != null && ctx.channel().isActive()) {
            ctx.writeAndFlush(new TextWebSocketFrame(message));
        } else {
            WsConstant.ctxWs.get(ctx).close(ctx.channel(), new CloseWebSocketFrame());
        }
    }

    /*
    public static void main(String[] args) {
        try {
            WebSocketClient myWebsocketClient = new WebsocketClientImpl("http://192.168.100.88:7601/demo/websocket", null);
            myWebsocketClient.connect();
            while(!myWebsocketClient.getReadyState().equals(ReadyState.OPEN)){
                System.out.println("还没有打开");
            }
            System.out.println("打开了");
            myWebsocketClient.send("111111");
            Thread.currentThread().join();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    */
}