package com.polaris.container.gateway.proxy.impl;

import com.polaris.container.gateway.proxy.SslEngineSource;

import io.netty.channel.ChannelPipeline;
import io.netty.handler.traffic.GlobalTrafficShapingHandler;

public abstract class ClientToProxyConnectionFactory {

    public static ClientToProxyConnection create(
            DefaultHttpProxyServer proxyServer, 
            SslEngineSource sslEngineSource, 
            ChannelPipeline pipeline, 
            GlobalTrafficShapingHandler globalTrafficShapingHandler) {
        return new ClientToProxyConnectionWithHttp2(
                proxyServer,
                sslEngineSource,
                pipeline,
                globalTrafficShapingHandler);
    }
}
