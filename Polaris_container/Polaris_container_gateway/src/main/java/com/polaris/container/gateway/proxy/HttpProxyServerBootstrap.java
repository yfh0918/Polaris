package com.polaris.container.gateway.proxy;

import java.net.InetSocketAddress;

import com.polaris.container.gateway.proxy.impl.ThreadPoolConfiguration;

/**
 * Configures and starts an {@link HttpProxyServer}. The HttpProxyServer is
 * built using {@link #start()}. Sensible defaults are available for all
 * parameters such that {@link #start()} could be called immediately if you
 * wish.
 */
public interface HttpProxyServerBootstrap {

    /**
     * <p>
     * Give the server a name (used for naming threads, useful for logging).
     * </p>
     * 
     * <p>
     * Default = LittleProxy
     * </p>
     * 
     * @param name
     * @return
     */
    HttpProxyServerBootstrap withName(String name);

    /**
     * <p>
     * Specify the {@link TransportProtocol} to use for incoming connections.
     * </p>
     * 
     * <p>
     * Default = TCP
     * </p>
     * 
     * @param transportProtocol
     * @return
     */
    HttpProxyServerBootstrap withTransportProtocol(
            TransportProtocol transportProtocol);

    /**
     * <p>
     * Listen for incoming connections on the given address.
     * </p>
     * 
     * <p>
     * Default = [bound ip]:8080
     * </p>
     * 
     * @param address
     * @return
     */
    HttpProxyServerBootstrap withAddress(InetSocketAddress address);

    /**
     * <p>
     * Listen for incoming connections on the given port.
     * </p>
     * 
     * <p>
     * Default = 8080
     * </p>
     * 
     * @param port
     * @return
     */
    HttpProxyServerBootstrap withPort(int port);

    /**
     * Sets the alias to use when adding Via headers to incoming and outgoing HTTP messages. The alias may be any
     * pseudonym, or if not specified, defaults to the hostname of the local machine. See RFC 7230, section 5.7.1.
     *
     * @param alias the pseudonym to add to Via headers
     */
    HttpProxyServerBootstrap withProxyAlias(String alias);
    
    /**
     * <p>
     * Specify whether or not to only allow local connections.
     * </p>
     * 
     * <p>
     * Default = true
     * </p>
     * 
     * @param allowLocalOnly
     * @return
     */
    HttpProxyServerBootstrap withAllowLocalOnly(boolean allowLocalOnly);


    /**
     * <p>
     * Specify an {@link SslEngineSource} to use for encrypting inbound
     * connections. Enabling this will enable SSL client authentication
     * by default (see {@link #withAuthenticateSslClients(boolean)})
     * </p>
     * 
     * <p>
     * Default = null
     * </p>
     * 
     * <p>
     * Note - This and {@link #withManInTheMiddle(MitmManager)} are
     * mutually exclusive.
     * </p>
     * 
     * @param sslEngineSource
     * @return
     */
    HttpProxyServerBootstrap withSslEngineSource(
            SslEngineSource sslEngineSource);

    /**
     * <p>
     * Specify a {@link ProxyAuthenticator} to use for doing basic HTTP
     * authentication of clients.
     * </p>
     * 
     * <p>
     * Default = null
     * </p>
     * 
     * @param proxyAuthenticator
     * @return
     */
    HttpProxyServerBootstrap withProxyAuthenticator(
            ProxyAuthenticator proxyAuthenticator);

    /**
     * <p>
     * Specify a {@link HttpFiltersSource} to use for filtering requests and/or
     * responses through this proxy.
     * </p>
     * 
     * <p>
     * Default = null
     * </p>
     * 
     * @param filtersSource
     * @return
     */
    HttpProxyServerBootstrap withFiltersSource(
            HttpFiltersSource filtersSource);

    /**
     * <p>
     * Specify whether or not to run this proxy as a transparent proxy.
     * </p>
     * 
     * <p>
     * Default = false
     * </p>
     * 
     * @param transparent
     * @return
     */
    HttpProxyServerBootstrap withTransparent(
            boolean transparent);

    /**
     * <p>
     * Specify the timeout after which to disconnect idle connections, in
     * seconds.
     * </p>
     * 
     * <p>
     * Default = 70
     * </p>
     * 
     * @param idleConnectionTimeout
     * @return
     */
    HttpProxyServerBootstrap withIdleConnectionTimeout(
            int idleConnectionTimeout);

    /**
     * <p>
     * Specify the timeout for connecting to the upstream server on a new
     * connection, in milliseconds.
     * </p>
     * 
     * <p>
     * Default = 40000
     * </p>
     * 
     * @param connectTimeout
     * @return
     */
    HttpProxyServerBootstrap withConnectTimeout(
            int connectTimeout);

    /**
     * Specify a custom {@link HostResolver} for resolving server addresses.
     * 
     * @param serverResolver
     * @return
     */
    HttpProxyServerBootstrap withServerResolver(HostResolver serverResolver);

    /**
     * <p>
     * Add an {@link ActivityTracker} for tracking activity in this proxy.
     * </p>
     * 
     * @param activityTracker
     * @return
     */
    HttpProxyServerBootstrap plusActivityTracker(ActivityTracker activityTracker);

    /**
     * <p>
     * Specify the read and/or write bandwidth throttles for this proxy server. 0 indicates not throttling.
     * </p>
     * @param readThrottleBytesPerSecond
     * @param writeThrottleBytesPerSecond
     * @return
     */
    HttpProxyServerBootstrap withThrottling(long readThrottleBytesPerSecond, long writeThrottleBytesPerSecond);

    /**
     * All outgoing-communication of the proxy-instance is goin' to be routed via the given network-interface
     *
     * @param inetSocketAddress to be used for outgoing communication
     */
    HttpProxyServerBootstrap withNetworkInterface(InetSocketAddress inetSocketAddress);
    
    HttpProxyServerBootstrap withMaxInitialLineLength(int maxInitialLineLength);
    
    HttpProxyServerBootstrap withMaxHeaderSize(int maxHeaderSize);
    
    HttpProxyServerBootstrap withMaxChunkSize(int maxChunkSize);

    /**
     * <p>
     * Build and starts the server.
     * </p>
     *
     * @return the newly built and started server
     */
    HttpProxyServer start();
    
    /**
     * <p>
     * Build and graceful stop the server.
     * </p>
     *
     */
    void stop();
    
    /**
     * <p>
     * Build and non graceful stop the server.
     * </p>
     *
     */
    void abort();

    /**
     * Set the configuration parameters for the proxy's thread pools.
     *
     * @param configuration thread pool configuration
     * @return proxy server bootstrap for chaining
     */
    HttpProxyServerBootstrap withThreadPoolConfiguration(ThreadPoolConfiguration configuration);
}