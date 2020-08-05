package com.polaris.container.gateway.proxy.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.container.gateway.proxy.SslEngineSource;
import com.polaris.container.gateway.proxy.websocket.WsHandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.traffic.GlobalTrafficShapingHandler;

public class ClientToProxyConnectionAdapt  extends ClientToProxyConnection{
    
    private static final Logger log = LoggerFactory.getLogger(ClientToProxyConnectionAdapt.class);

    private WsHandler wsHandler;

    ClientToProxyConnectionAdapt(DefaultHttpProxyServer proxyServer, SslEngineSource sslEngineSource, boolean authenticateClients,
            ChannelPipeline pipeline, GlobalTrafficShapingHandler globalTrafficShapingHandler) {
        super(proxyServer, sslEngineSource, authenticateClients, pipeline, globalTrafficShapingHandler);
        this.wsHandler = new WsHandler();
    }

    /**
     * 功能：读取服务器发送过来的信息
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if ((HttpRequest) msg instanceof HttpRequest) {// 如果是HTTP请求，进行HTTP操作
           log.debug("进入http处理");
           String serverHostAndPort = identifyHostAndPort((HttpRequest) msg);
           if (!wsHandler.upgrade((HttpRequest) msg, ctx, serverHostAndPort)) {
               super.channelRead0(ctx, msg);
           }
        } else if ((HttpObject) msg instanceof HttpObject) {
            log.debug("进入http处理");
            super.channelRead0(ctx, msg);
        } else if (msg instanceof WebSocketFrame) {
            log.debug("进入websocket处理");
            wsHandler.handle(ctx, (WebSocketFrame) msg);
        }
    }

   
}
