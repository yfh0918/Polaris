package com.polaris.container.gateway.proxy.websocket.client;

import org.java_websocket.exceptions.WebsocketNotConnectedException;

import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public interface WebSocketClient {
    
    /**  connect */
    void connect();
    
    /**  close */
    void close();
    
    
    /**
     * Send a ping to the other end
     * @throws WebsocketNotConnectedException websocket is not yet connected
     */
    void sendPing(PingWebSocketFrame frame);
    
    /**
     * Send a ping to the other end
     * @throws WebsocketNotConnectedException websocket is not yet connected
     */
    void sendPong(PongWebSocketFrame frame);
    
    
    /**
     * Send Binary data (plain bytes) to the other end.
     *
     * @param bytes the binary data to send
     * @throws IllegalArgumentException the data is null
     * @throws WebsocketNotConnectedException websocket is not yet connected
     */
    void send( BinaryWebSocketFrame frame );
    
    /**
     * Sends <var>text</var> to the connected websocket server.
     *
     * @param text
     *            The string which will be transmitted.
     */
    void send( TextWebSocketFrame frame );
    
    /**
     * This represents the state of the connection.
     */
    WebSocketStatus getState();
    
    /**
     * add Idle Connect Timeout and get it
     */
    int addAndGetIdleConnectTimeout(int idleConnectTimeout);
    
    /**
     * reset Idle Connect Timeout
     */
    void resetIdleConnectTimeout();
}
