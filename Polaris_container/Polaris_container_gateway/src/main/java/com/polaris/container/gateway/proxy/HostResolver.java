package com.polaris.container.gateway.proxy;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import com.polaris.container.gateway.proxy.impl.ProxyToServerConnection;

/**
 * Resolves host and port into an InetSocketAddress.
 */
public interface HostResolver {
    public InetSocketAddress resolve(String host, int port, String uri)
            throws UnknownHostException;
}
