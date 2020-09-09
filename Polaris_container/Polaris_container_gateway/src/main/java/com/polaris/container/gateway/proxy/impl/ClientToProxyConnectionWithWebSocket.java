package com.polaris.container.gateway.proxy.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.container.gateway.pojo.HttpRequestWrapper;
import com.polaris.container.gateway.proxy.SslEngineSource;
import com.polaris.container.gateway.proxy.TransportProtocol;
import com.polaris.container.gateway.proxy.websocket.WebSocketHandlerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.traffic.GlobalTrafficShapingHandler;

public class ClientToProxyConnectionWithWebSocket  extends ClientToProxyConnection{
    private static Logger logger = LoggerFactory.getLogger(ClientToProxyConnectionWithWebSocket.class);

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
        } else if (msg instanceof HttpRequestWrapper) {
            if (WebSocketHandlerFactory.get().isWSProtocol((HttpRequestWrapper) msg)) {
                WebSocketHandlerFactory.get().upgrade(
                        (HttpRequestWrapper) msg, 
                        ctx, 
                        this.proxyServer.getServerResolver(),
                        this.proxyServer.getProxyToServerWorkerFor(TransportProtocol.TCP),
                        this.getHttpFiltersFromProxyServer((HttpRequestWrapper) msg));
                return;
            } 
        } 
        super.channelRead0(ctx, msg);
    }
    
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
            throws Exception {
        logger.debug("userEventTriggered");
        if (!WebSocketHandlerFactory.get().userEventTriggered(ctx,this.proxyServer.getIdleConnectionTimeout())) {
            return;
        }
        super.userEventTriggered(ctx, evt);
    }
}
