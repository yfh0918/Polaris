package com.polaris.container.gateway.proxy.websocket.client;

import java.nio.ByteBuffer;

import org.java_websocket.exceptions.WebsocketNotConnectedException;

import com.polaris.container.gateway.proxy.websocket.WsStatus;

public interface WebSocketInf {
    
    /**  connect */
    void connect();
    
    /**  close */
    void close();
    
    
    /**
     * Send a ping to the other end
     * @throws WebsocketNotConnectedException websocket is not yet connected
     */
    void sendPing();
    
    /**
     * Send Binary data (plain bytes) to the other end.
     *
     * @param bytes the binary data to send
     * @throws IllegalArgumentException the data is null
     * @throws WebsocketNotConnectedException websocket is not yet connected
     */
    void send( ByteBuffer bytes );
    
    /**
     * Sends <var>text</var> to the connected websocket server.
     *
     * @param text
     *            The string which will be transmitted.
     */
    void send( String text );
    
    /**
     * This represents the state of the connection.
     */
    WsStatus getState();
}
