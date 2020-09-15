package com.polaris.container.gateway.proxy.websocket;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.polaris.container.gateway.pojo.HttpRequestWrapper;
import com.polaris.container.gateway.proxy.HostResolver;
import com.polaris.container.gateway.proxy.websocket.client.WebSocketClient;
import com.polaris.container.gateway.proxy.websocket.client.WebSocketClientFactory;
import com.polaris.core.pojo.KeyValuePair;
import com.polaris.core.pojo.ServerHost;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;

public class WebSocketSupport {
    
    public static boolean isWSProtocol(HttpRequest req) {
        HttpHeaders headers = req.headers();
        if (headers == null) {
            return false;
        }
        String connection = headers.get("Connection");
        if (connection == null) {
            connection = headers.get("connection");
        }
        String upgrade = headers.get("Upgrade");
        if (upgrade == null) {
            upgrade = headers.get("upgrade");
        }
        if ("upgrade".equalsIgnoreCase(connection) && "websocket".equalsIgnoreCase(upgrade)) {
            return true;
        }
        return false;
    }
    
    
    /**
     * put websocket's parameter into header for token authentication  
     * 
     * @param httpRequest
     * @return
     */
    public static HttpRequest convertParameter(HttpRequest req) {
        List<KeyValuePair> list = ServerHost.getKeyValuePairs(req.uri());
        for (KeyValuePair kv : list) {
            req.headers().add(kv.getKey(), kv.getValue());
        }
        return req;
    }
    
    /**
     * create client for connect remote websocket
     * 
     * @param httpRequest
     * @return
     */
    public static boolean createWSClient(HttpRequestWrapper req, 
                                         ChannelHandlerContext ctx, 
                                         HostResolver hostResolver, 
                                         EventLoopGroup eventLoopGroup,
                                         WebSocketServerHandshaker handshaker) {
        
        try {
            //context
            InetSocketAddress address = hostResolver.resolve(req.getHttpProxy());

            //host
            String host = hostToString(address);
            
            //replace http request host for remote connection
            replaceProxyHostForRemoteConnenct(req,host);
            
            //proxy
            String websocketStr = ServerHost.HTTP_PREFIX +host + req.uri();
            WebSocketClient client = WebSocketClientFactory.create(websocketStr, eventLoopGroup, ctx);
            client.connect();
            for (int i = 0; i < 10 ; i++) {
                if (client.getState().equals(WebSocketStatus.OPEN)) {
                    handshaker.handshake(ctx.channel(), req);
                    WebSocketAdmin.create()
                                  .setWebSocketClient(client)
                                  .setWebSocketServerHandshaker(handshaker)
                                  .setChannelHandlerContext(ctx);
                    return true;
                }
                TimeUnit.MILLISECONDS.sleep(200);
            }
            client.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
    * replace http request host for remote connection
    * @return
    */
    private static void replaceProxyHostForRemoteConnenct(HttpRequest httpRequest, String host) {
        httpRequest.headers().remove(HttpHeaderNames.HOST.toString());
        httpRequest.headers().add(HttpHeaderNames.HOST.toString(), host);
    }
    
    private static String hostToString(InetSocketAddress address) {
        String host = address.getHostName();
        int port = address.getPort();
        if (port != 80) {
            host = host + ":" + port;
        }
        return host;
    }
    
}
