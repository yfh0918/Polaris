package com.polaris.container.gateway.proxy;

import com.polaris.container.gateway.proxy.impl.ClientToProxyConnection;
import com.polaris.container.gateway.proxy.impl.ProxyToServerConnection;

/**
 * Extension of {@link FlowContext} that provides additional information (which
 * we know after actually processing the request from the client).
 */
public class FullFlowContext extends FlowContext {
    private final String serverHostAndPort;
    private final ChainedProxy chainedProxy;
    private final ClientToProxyConnection clientConnection;
    private final ProxyToServerConnection serverConnection;
    

    public FullFlowContext(ClientToProxyConnection clientConnection,
            ProxyToServerConnection serverConnection) {
        super(clientConnection);
        this.serverHostAndPort = serverConnection.getServerHostAndPort();
        this.chainedProxy = serverConnection.getChainedProxy();
        this.clientConnection = clientConnection;
        this.serverConnection = serverConnection;
    }

    /**
     * The host and port for the server (i.e. the ultimate endpoint).
     * 
     * @return
     */
    public String getServerHostAndPort() {
        return serverHostAndPort;
    }

    /**
     * The chained proxy (if proxy chaining).
     * 
     * @return
     */
    public ChainedProxy getChainedProxy() {
        return chainedProxy;
    }

	public ClientToProxyConnection getClientConnection() {
		return clientConnection;
	}

	public ProxyToServerConnection getServerConnection() {
		return serverConnection;
	}

}
