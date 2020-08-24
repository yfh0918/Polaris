package com.polaris.container.gateway.proxy.websocket;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import com.google.common.net.HostAndPort;
import com.polaris.container.gateway.pojo.HttpHostContext;
import com.polaris.container.gateway.proxy.HostResolver;
import com.polaris.container.gateway.proxy.impl.ProxyUtils;
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
     * Identify the host and port for a request.
     * 
     * @param httpRequest
     * @return
     */
    public static String identifyHostAndPort(HttpRequest httpRequest) {
        String hostAndPort = ProxyUtils.parseHostAndPort(httpRequest);
        if (StringUtils.isBlank(hostAndPort)) {
            List<String> hosts = httpRequest.headers().getAll(
                    HttpHeaderNames.HOST.toString());
            if (hosts != null && !hosts.isEmpty()) {
                hostAndPort = hosts.get(0);
            }
        }
        return hostAndPort;
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
    public static boolean createWSClient(HttpRequest req, 
                                         ChannelHandlerContext ctx, 
                                         HostResolver hostResolver, 
                                         EventLoopGroup eventLoopGroup,
                                         String serverHostAndPort,
                                         WebSocketServerHandshaker handshaker) {
        
        try {
            //context
            String contextPath = HttpHostContext.getContextPath(req.uri());
            HostAndPort parsedHostAndPort = HostAndPort.fromString(serverHostAndPort);
            InetSocketAddress address = hostResolver.resolve(parsedHostAndPort.getHost(), parsedHostAndPort.getPortOrDefault(80), contextPath);
            String websocketStr = ServerHost.HTTP_PREFIX +address.getHostName() + ":" + address.getPort() + req.uri();
            WebSocketClient client = WebSocketClientFactory.create(websocketStr, eventLoopGroup, ctx);
            client.connect();
            for (int i = 0; i < 10 ; i++) {
                if (client.getState().equals(WebSocketStatus.OPEN)) {
                    handshaker.handshake(ctx.channel(), req);
                    new WebSocketAdmin().setWebSocketClient(client)
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
    
}
