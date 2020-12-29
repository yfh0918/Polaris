package com.polaris.container.gateway.proxy.impl;

import com.polaris.container.gateway.pojo.HttpProtocolForHttp2;
import com.polaris.container.gateway.proxy.SslEngineSource;

import io.netty.channel.ChannelPipeline;
import io.netty.handler.traffic.GlobalTrafficShapingHandler;

public abstract class ClientToProxyConnectionFactory {

    public static ClientToProxyConnection create(
            DefaultHttpProxyServer proxyServer, 
            SslEngineSource sslEngineSource, 
            ChannelPipeline pipeline, 
            GlobalTrafficShapingHandler globalTrafficShapingHandler) {
        if (HttpProtocolForHttp2.isHttp20Enable()) {
            return new ClientToProxyConnectionWithHttp2(
                    proxyServer,
                    sslEngineSource,
                    pipeline,
                    globalTrafficShapingHandler);
        }
        return new ClientToProxyConnectionWithWebSocket(
                proxyServer,
                sslEngineSource,
                pipeline,
                globalTrafficShapingHandler);
        
    }
}
