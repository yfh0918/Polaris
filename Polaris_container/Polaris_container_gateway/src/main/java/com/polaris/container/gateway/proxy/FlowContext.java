package com.polaris.container.gateway.proxy;

import java.net.InetSocketAddress;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSession;

import com.polaris.container.gateway.proxy.impl.ClientToProxyConnection;

/**
 * <p>
 * Encapsulates contextual information for flow information that's being
 * reported to a {@link ActivityTracker}.
 * </p>
 */
public class FlowContext {
    private final ClientToProxyConnection clientConnection;
    public FlowContext(ClientToProxyConnection clientConnection) {
        super();
        this.clientConnection = clientConnection;
    }

    /**
     * The address of the client.
     * 
     * @return
     */
    public InetSocketAddress getClientAddress() {
        return clientConnection.getClientAddress();
    }

    /**
     * If using SSL, this returns the {@link SSLSession} on the client
     * connection.
     * 
     * @return
     */
    public SSLSession getClientSslSession() {
        SSLEngine sslEngine = clientConnection.getSslEngine();
        return sslEngine != null ? sslEngine.getSession() : null;
    }

}
