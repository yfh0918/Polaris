package com.polaris.container.gateway.proxy.impl;

import com.polaris.container.gateway.proxy.SslEngineSource;
import com.polaris.container.gateway.proxy.http2.Http11Listener;
import com.polaris.container.gateway.proxy.http2.Http2ChannelInboundHandlerAdapter;
import com.polaris.container.gateway.proxy.http2.Http2EventTriggerHandler;
import com.polaris.container.gateway.proxy.http2.Http2OrHttpHandler;
import com.polaris.container.gateway.proxy.http2.Http2UpgradeCodecFactory;
import com.polaris.core.config.ConfClient;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerUpgradeHandler;
import io.netty.handler.codec.http.HttpServerUpgradeHandler.UpgradeCodecFactory;
import io.netty.handler.codec.http2.CleartextHttp2ServerUpgradeHandler;
import io.netty.handler.traffic.GlobalTrafficShapingHandler;

/**
 * Sets up the Netty pipeline for the example server. Depending on the endpoint config, sets up the
 * pipeline for NPN or cleartext HTTP upgrade to HTTP/2.
 */
public class ClientToProxyConnectionWithHttp2 extends ClientToProxyConnectionWithWebSocket implements Http11Listener{
    private static final UpgradeCodecFactory upgradeCodecFactory = new Http2UpgradeCodecFactory();
    
    public ClientToProxyConnectionWithHttp2(
            DefaultHttpProxyServer proxyServer, 
            SslEngineSource sslEngineSource, 
            ChannelPipeline pipeline, 
            GlobalTrafficShapingHandler globalTrafficShapingHandler) {
        super(proxyServer, sslEngineSource, pipeline, globalTrafficShapingHandler);
    }

    /**
     * Configure the pipeline for TLS NPN negotiation to HTTP/2.
     */
    private void configureSsl(Channel ch) {
        ch.pipeline().addLast(new Http2OrHttpHandler(this));
    }

    /**
     * Configure the pipeline for a cleartext upgrade from HTTP to HTTP/2.0
     */
    private void configureClearText(Channel ch) {
        final ChannelPipeline p = ch.pipeline();
        final HttpServerCodec sourceCodec = new HttpServerCodec();
        final HttpServerUpgradeHandler upgradeHandler = new HttpServerUpgradeHandler(sourceCodec, upgradeCodecFactory);
        p.addLast(new CleartextHttp2ServerUpgradeHandler(sourceCodec, upgradeHandler,Http2ChannelInboundHandlerAdapter.INSTANCE));
        p.addLast(Http2EventTriggerHandler.NAME, Http2EventTriggerHandler.INSTANCE);
        onHttp11(p,true);
    }
    
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered0(ctx);
    }

    @Override
    protected void initChannelPipeline(ChannelPipeline pipeline) {
        boolean http2 = Boolean.parseBoolean(ConfClient.get("server.http2.enable","false"));
        boolean tls = Boolean.parseBoolean(ConfClient.get("server.tls.enable","false"));
        if (http2) {
            if (tls) {
                configureSsl(pipeline.channel());
            } else {
                configureClearText(pipeline.channel());
            }
            
        } else {
            onHttp11(pipeline, false);
        }
    }
    @Override
    public void onHttp11(ChannelPipeline pipeline, boolean removeHttp11ServerCodec) {
        super.initChannelPipeline(pipeline);
        if (removeHttp11ServerCodec) {
            pipeline.remove("encoder");
            pipeline.remove("decoder");
        }
    }
}

