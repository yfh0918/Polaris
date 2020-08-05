package com.polaris.container.gateway.proxy.impl;

import java.util.Objects;

import com.polaris.container.gateway.proxy.SslEngineSource;
import com.polaris.container.gateway.proxy.websocket.WsHandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.traffic.GlobalTrafficShapingHandler;

public class ClientToProxyConnectionAdapt  extends ClientToProxyConnection{
    
    private WsHandler wsHandler = new WsHandler();

    ClientToProxyConnectionAdapt(DefaultHttpProxyServer proxyServer, SslEngineSource sslEngineSource, boolean authenticateClients,
            ChannelPipeline pipeline, GlobalTrafficShapingHandler globalTrafficShapingHandler) {
        super(proxyServer, sslEngineSource, authenticateClients, pipeline, globalTrafficShapingHandler);
    }

    /**
     * 功能：读取服务器发送过来的信息
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            if (isWsRequest((HttpRequest) msg)) {
                String serverHostAndPort = identifyHostAndPort((HttpRequest) msg);
                if (wsHandler.upgrade((HttpRequest) msg, 
                        ctx, 
                        this.proxyServer.getServerResolver(),
                        this.getHttpFiltersFromProxyServer((HttpRequest) msg),
                        serverHostAndPort)) {
                }
                return;
            } 
            super.channelRead0(ctx, msg);
        } else if (msg instanceof HttpObject) {
            super.channelRead0(ctx, msg);
        } else if (msg instanceof WebSocketFrame) {
            wsHandler.handle(ctx, (WebSocketFrame) msg);
        }
    }
    
    private boolean isWsRequest(HttpRequest req) {
        HttpHeaders headers = req.headers();
        if (headers == null) {
            return false;
        }
        String connection = headers.get("Connection");
        String upgrade = headers.get("Upgrade");
        if (Objects.equals("Upgrade", connection) && Objects.equals("websocket", upgrade)) {
            return true;
        }
        return false;
    }
}
