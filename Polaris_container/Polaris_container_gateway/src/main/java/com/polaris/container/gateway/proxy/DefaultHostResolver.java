package com.polaris.container.gateway.proxy;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import io.netty.handler.codec.http.HttpRequest;

/**
 * Default implementation of {@link HostResolver} that just uses
 * {@link InetAddress#getByName(String)}.
 */
public class DefaultHostResolver implements HostResolver {
    @Override
    public InetSocketAddress resolve(String host, int port, HttpRequest originalRequest)
            throws UnknownHostException {
        InetAddress addr = InetAddress.getByName(host);
        return new InetSocketAddress(addr, port);
    }
}
