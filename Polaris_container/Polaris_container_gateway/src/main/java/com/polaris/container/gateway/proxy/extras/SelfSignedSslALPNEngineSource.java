package com.polaris.container.gateway.proxy.extras;

import javax.net.ssl.SSLEngine;

import com.polaris.container.gateway.proxy.SslEngineSource;

import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.ssl.SslContext;

public class SelfSignedSslALPNEngineSource implements SslEngineSource {
    private SslContext sslContext;
    public SelfSignedSslALPNEngineSource() {
        sslContext = SelfSignedSslALPNContextFactory.get();
    }
    
    @Override
    public SSLEngine newSslEngine(ByteBufAllocator alloc) {
        return sslContext.newEngine(alloc);
    }

    @Override
    public SSLEngine newSslEngine(ByteBufAllocator alloc, String peerHost, int peerPort) {
        return sslContext.newEngine(alloc, peerHost,peerPort);
    }

}
