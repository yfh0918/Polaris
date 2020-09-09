package com.polaris.container.gateway.proxy;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import com.polaris.container.gateway.pojo.HttpProxy;

/**
 * Resolves host and port into an InetSocketAddress.
 */
public interface HostResolver {
    public InetSocketAddress resolve(HttpProxy proxy)
            throws UnknownHostException;
}
