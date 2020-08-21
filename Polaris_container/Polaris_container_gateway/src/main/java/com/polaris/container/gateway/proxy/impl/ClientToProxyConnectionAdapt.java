package com.polaris.container.gateway.proxy.impl;

import com.esotericsoftware.minlog.Log;
import com.polaris.container.gateway.proxy.SslEngineSource;
import com.polaris.container.gateway.proxy.websocket.WebSocketHandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.traffic.GlobalTrafficShapingHandler;

public class ClientToProxyConnectionAdapt  extends ClientToProxyConnection{
    
    private WebSocketHandler wsHandler = new WebSocketHandler();

    ClientToProxyConnectionAdapt(DefaultHttpProxyServer proxyServer, SslEngineSource sslEngineSource, boolean authenticateClients,
            ChannelPipeline pipeline, GlobalTrafficShapingHandler globalTrafficShapingHandler) {
        super(proxyServer, sslEngineSource, authenticateClients, pipeline, globalTrafficShapingHandler);
    }

    /**
     * 功能：读取服务器发送过来的信息
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof WebSocketFrame) {
            wsHandler.handle(ctx, (WebSocketFrame) msg);
            return;
        } else if (msg instanceof HttpRequest) {
            if (wsHandler.isWsRequest((HttpRequest) msg)) {
                String serverHostAndPort = identifyHostAndPort((HttpRequest) msg);
                wsHandler.upgrade((HttpRequest) msg, 
                        ctx, 
                        this.proxyServer.getServerResolver(),
                        this.getHttpFiltersFromProxyServer((HttpRequest) msg),
                        serverHostAndPort);
                return;
            } 
        } 
        super.channelRead0(ctx, msg);
    }
    
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
            throws Exception {
        Log.debug("userEventTriggered");
        if (!wsHandler.userEventTriggered(ctx,this.proxyServer.getIdleConnectionTimeout())) {
            return;
        }
        super.userEventTriggered(ctx, evt);
    }
}
