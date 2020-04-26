package com.polaris.container.gateway.proxy;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import com.polaris.container.gateway.dnssec4j.VerifiedAddressFactory;

import io.netty.handler.codec.http.HttpRequest;

public class DnsSecServerResolver implements HostResolver {
    @Override
    public InetSocketAddress resolve(String host, int port, HttpRequest originalRequest)
            throws UnknownHostException {
        return VerifiedAddressFactory.newInetSocketAddress(host, port, true);
    }
}
