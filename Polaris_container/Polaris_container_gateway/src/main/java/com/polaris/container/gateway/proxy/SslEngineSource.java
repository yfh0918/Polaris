package com.polaris.container.gateway.proxy;

import javax.net.ssl.SSLEngine;

import io.netty.buffer.ByteBufAllocator;

/**
 * Source for {@link SSLEngine}s.
 */
public interface SslEngineSource {

    /**
     * Returns an {@link SSLEngine} to use for a server connection from
     * 
     * @return
     */
    SSLEngine newSslEngine(ByteBufAllocator alloc);

    /**
     * Returns an {@link SSLEngine} to use for a client connection from
     * 
     * Note: Peer information is needed to send the server_name extension in
     * handshake with Server Name Indication (SNI).
     * 
     * @param peerHost
     *            to start a client connection to the server.
     * @param peerPort
     *            to start a client connection to the server.
     * @return
     */
    SSLEngine newSslEngine(ByteBufAllocator alloc, String peerHost, int peerPort);
}
