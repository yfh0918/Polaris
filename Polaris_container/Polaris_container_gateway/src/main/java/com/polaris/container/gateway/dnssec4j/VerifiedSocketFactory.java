package com.polaris.container.gateway.dnssec4j;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.SocketFactory;

/**
 * {@link SocketFactory} that wraps another delegate {@link SocketFactory}
 * and intercepts all factory calls to verify the DNSSEC records of hosts
 * before creating sockets.
 */
public class VerifiedSocketFactory extends SocketFactory {

    
    private final SocketFactory delegate;

    public VerifiedSocketFactory(final SocketFactory delegate) {
        this.delegate = delegate;
    }
    
    @Override
    public Socket createSocket(final String host, final int port) 
        throws IOException, UnknownHostException {
        try {
            final InetAddress isa = DnsSec.getByName(host);
            return this.delegate.createSocket(isa, port);
        } catch (final DNSSECException e) {
            throw new IOException("DNSSEC verification error!!", e);
        }
    }

    @Override
    public Socket createSocket(final InetAddress host, final int port) 
        throws IOException {
        try {
            final InetAddress isa = DnsSec.getByName(host.getHostName());
            return this.delegate.createSocket(isa, port);
        } catch (final DNSSECException e) {
            throw new IOException("DNSSEC verification error!!", e);
        }
    }

    @Override
    public Socket createSocket(final String host, final int port, 
        final InetAddress localHost, final int localPort)
        throws IOException, UnknownHostException {
        try {
            final InetAddress isa = DnsSec.getByName(host);
            return this.delegate.createSocket(isa, port, localHost, localPort);
        } catch (final DNSSECException e) {
            throw new IOException("DNSSEC verification error!!", e);
        }
    }

    @Override
    public Socket createSocket(final InetAddress host, final int port, 
        final InetAddress localAddress, final int localPort) throws IOException {
        try {
            final InetAddress isa = DnsSec.getByName(host.getHostName());
            return this.delegate.createSocket(isa, port, localAddress, localPort);
        } catch (final DNSSECException e) {
            throw new IOException("DNSSEC verification error!!", e);
        }
    }

}
