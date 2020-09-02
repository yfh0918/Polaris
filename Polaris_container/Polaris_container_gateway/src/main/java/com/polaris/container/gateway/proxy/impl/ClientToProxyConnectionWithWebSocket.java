package com.polaris.container.gateway.proxy.impl;

import com.esotericsoftware.minlog.Log;
import com.polaris.container.gateway.proxy.SslEngineSource;
import com.polaris.container.gateway.proxy.TransportProtocol;
import com.polaris.container.gateway.proxy.websocket.WebSocketHandlerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.traffic.GlobalTrafficShapingHandler;

public class ClientToProxyConnectionWithWebSocket  extends ClientToProxyConnection{
    
    public ClientToProxyConnectionWithWebSocket(
            DefaultHttpProxyServer proxyServer, 
            SslEngineSource sslEngineSource, 
            ChannelPipeline pipeline, 
            GlobalTrafficShapingHandler globalTrafficShapingHandler) {
        super(proxyServer, sslEngineSource, pipeline, globalTrafficShapingHandler);
    }

    /**
     * 功能：读取服务器发送过来的信息
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof WebSocketFrame) {
            WebSocketHandlerFactory.get().proxyToServer(ctx, (WebSocketFrame) msg);
            return;
        } else if (msg instanceof HttpRequest) {
            if (WebSocketHandlerFactory.get().isWSProtocol((HttpRequest) msg)) {
                WebSocketHandlerFactory.get().upgrade(
                        (HttpRequest) msg, 
                        ctx, 
                        this.proxyServer.getServerResolver(),
                        this.proxyServer.getProxyToServerWorkerFor(TransportProtocol.TCP),
                        this.getHttpFiltersFromProxyServer((HttpRequest) msg));
                return;
            } 
        } 
        super.channelRead0(ctx, msg);
    }
    
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
            throws Exception {
        Log.debug("userEventTriggered");
        if (!WebSocketHandlerFactory.get().userEventTriggered(ctx,this.proxyServer.getIdleConnectionTimeout())) {
            return;
        }
        super.userEventTriggered(ctx, evt);
    }
}
