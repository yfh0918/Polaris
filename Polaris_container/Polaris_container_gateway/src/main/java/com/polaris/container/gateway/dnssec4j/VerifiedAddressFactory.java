package com.polaris.container.gateway.dnssec4j;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for creating addresses. This class is DNSSEC-aware, so will attempt
 * to use DNSSEC if configured to do so.
 */
public class VerifiedAddressFactory {
    
    private static final Logger LOG = 
        LoggerFactory.getLogger(VerifiedAddressFactory.class);

    /**
     * Creates a new InetSocketAddress, verifying the host with DNSSEC if 
     * configured to do so.
     * 
     * @param host The host.
     * @param port The port.
     * @return The endpoint.
     * @throws UnknownHostException If the host cannot be resolved.
     */
    public static InetSocketAddress newInetSocketAddress(final String host, 
        final int port) throws UnknownHostException {
        return newInetSocketAddress(host, port, true);
    }
    
    /**
     * Creates a new InetSocketAddress, verifying the host with DNSSEC if 
     * configured to do so.
     * 
     * @param host The host.
     * @param port The port.
     * @param useDnsSec Whether or not to use DNSSEC.
     * @return The endpoint.
     * @throws UnknownHostException If the host cannot be resolved.
     */
    public static InetSocketAddress newInetSocketAddress(final String host, 
        final int port, final boolean useDnsSec) throws UnknownHostException {

        return new InetSocketAddress(newVerifiedInetAddress(host, useDnsSec), port);
    }

    /**
     * Creates a new InetSocket, verifying the host with DNSSEC if 
     * configured to do so.
     * 
     * @param host The host.
     * @param useDnsSec Whether or not to use DNSSEC.
     * @return The {@link InetAddress}.
     * @throws UnknownHostException If the host cannot be resolved.
     */
    public static InetAddress newVerifiedInetAddress(final String host,
        final boolean useDnsSec) throws UnknownHostException {
        if (useDnsSec) {
            try {
                return DnsSec.getByName(host);
            } catch (final IOException e) {
                LOG.info("Could not resolve address for: "+host, e);
            } catch (final DNSSECException e) {
                LOG.warn("DNSSEC error. Bad signature?", e);
                throw new Error("DNSSEC error. Bad signature?", e);
            }
        }
        return InetAddress.getByName(host);
    }

}
