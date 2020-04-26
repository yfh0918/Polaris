package com.polaris.container.gateway.proxy;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import io.netty.handler.codec.http.HttpRequest;

/**
 * Resolves host and port into an InetSocketAddress.
 */
public interface HostResolver {
    public InetSocketAddress resolve(String host, int port, HttpRequest originalRequest)
            throws UnknownHostException;
}
