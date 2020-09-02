package com.polaris.container.gateway.proxy;

import com.polaris.container.gateway.proxy.impl.ClientToProxyConnection;
import com.polaris.container.gateway.proxy.impl.ProxyToServerConnection;

/**
 * Extension of {@link FlowContext} that provides additional information (which
 * we know after actually processing the request from the client).
 */
public class FullFlowContext extends FlowContext {
    private final ClientToProxyConnection clientConnection;
    private final ProxyToServerConnection serverConnection;
    

    public FullFlowContext(ClientToProxyConnection clientConnection,
            ProxyToServerConnection serverConnection) {
        super(clientConnection);
        this.clientConnection = clientConnection;
        this.serverConnection = serverConnection;
    }

    /**
     * The host and port for the server (i.e. the ultimate endpoint).
     * 
     * @return
     */
    public String getServerHostAndPort() {
        return serverConnection.getServerHostAndPort();
    }

	public ClientToProxyConnection getClientConnection() {
		return clientConnection;
	}

	public ProxyToServerConnection getServerConnection() {
		return serverConnection;
	}

}
