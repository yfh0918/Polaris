package com.polaris.container.gateway.proxy.websocket;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.net.HostAndPort;
import com.polaris.container.gateway.pojo.HttpHostContext;
import com.polaris.container.gateway.proxy.HostResolver;
import com.polaris.container.gateway.proxy.HttpFilters;
import com.polaris.container.gateway.proxy.websocket.client.WebSocketClientFactory;
import com.polaris.container.gateway.proxy.websocket.client.WebSocketClient;
import com.polaris.core.pojo.KeyValuePair;
import com.polaris.core.pojo.ServerHost;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;

public class WebSocketHandler {
    
    private static final Logger log = LoggerFactory.getLogger(WebSocketHandler.class);
    
    /**
     * 处理http请求为websocket握手时升级为websocket
     * */
    public void upgrade(
            HttpRequest req, 
            ChannelHandlerContext ctx, 
            HostResolver hostResolver, 
            EventLoopGroup eventLoopGroup,
            HttpFilters filters, 
            String serverHostAndPort) {
        log.debug("websocket connecting...");
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
                req.uri(), null, false);
        WebSocketServerHandshaker handshaker = wsFactory.newHandshaker(req);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            
            //最大连接数
            if (WebSocketAdmin.size() > WebSocketConfigReader.getRequestMaxNumber()) {
                WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
                return;
            }
            
            //建立http连接需要filter
            List<KeyValuePair> list = ServerHost.getKeyValuePairs(req.uri());
            for (KeyValuePair kv : list) {
                req.headers().add(kv.getKey(), kv.getValue());//websokcet认证参数设置到header中
            }
            HttpResponse response = filters.clientToProxyRequest(req);
            if (response != null) {
                
                //filter不通过，直接返回
                ctx.writeAndFlush(response,ctx.channel().newPromise());
            } else {
                //与远程的websocket建立连接
                WebSocketClient client = proxyToServer(req, ctx, hostResolver,eventLoopGroup, serverHostAndPort);
                if (client != null) {
                    handshaker.handshake(ctx.channel(), req);
                    new WebSocketAdmin().setWebSocketClient(client)
                                 .setWebSocketServerHandshaker(handshaker)
                                 .setChannelHandlerContext(ctx);
                } else {
                    WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
                }  
            }
        }
        return;
    }

    public void handle(ChannelHandlerContext ctx, WebSocketFrame frame) {
        
        //close
        if (frame instanceof CloseWebSocketFrame) {
            WebSocketAdmin.close(ctx);
            return;
        }
        
        // ping
        if (frame instanceof PingWebSocketFrame) {
            WebSocketAdmin wsComponent = WebSocketAdmin.get(ctx);
            if (wsComponent != null) {
                wsComponent.getWebSocketClient().sendPing((PingWebSocketFrame)frame);
            } else {
                WebSocketAdmin.close(ctx);
            }
            return;
        }
        
        // ping
        else if (frame instanceof PongWebSocketFrame) {
            WebSocketAdmin wsComponent = WebSocketAdmin.get(ctx);
            if (wsComponent != null) {
                wsComponent.getWebSocketClient().sendPong((PongWebSocketFrame)frame);
            } else {
                WebSocketAdmin.close(ctx);
            }
            return;
        }

        // text
        else if (frame instanceof TextWebSocketFrame) {
            WebSocketAdmin wsComponent = WebSocketAdmin.get(ctx);
            if (wsComponent != null) {
                wsComponent.getWebSocketClient().send(((TextWebSocketFrame) frame));
            } else {
                WebSocketAdmin.close(ctx);
            }
            return;
        }
        
        //byte
        else if (frame instanceof BinaryWebSocketFrame) {
            WebSocketAdmin wsComponent = WebSocketAdmin.get(ctx);
            if (wsComponent != null) {
                wsComponent.getWebSocketClient().send((BinaryWebSocketFrame) frame);
            } else {
                WebSocketAdmin.close(ctx);
            }
            return;
        }
    }

    private WebSocketClient proxyToServer(HttpRequest req, 
            ChannelHandlerContext ctx, 
            HostResolver hostResolver, 
            EventLoopGroup eventLoopGroup,
            String serverHostAndPortt) {
        try {
            //context
            String contextPath = HttpHostContext.getContextPath(req.uri());
            HostAndPort parsedHostAndPort = HostAndPort.fromString(serverHostAndPortt);
            InetSocketAddress address = hostResolver.resolve(parsedHostAndPort.getHost(), parsedHostAndPort.getPortOrDefault(80), contextPath);
            String websocketStr = ServerHost.HTTP_PREFIX +address.getHostName() + ":" + address.getPort() + req.uri();
            WebSocketClient client = WebSocketClientFactory.create(websocketStr, eventLoopGroup, ctx);
            client.connect();
            for (int i = 0; i < 10 ; i++) {
                if (client.getState().equals(WebSocketStatus.OPEN)) {
                    return client;
                }
                TimeUnit.MILLISECONDS.sleep(200);
            }
            client.close();
        } catch (Exception e) {
            log.error("ERROR:",e);
        }
        
        return null;
    }
    
    public boolean isWsRequest(HttpRequest req) {
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
    
    public boolean userEventTriggered(ChannelHandlerContext ctx,int idleConnectTimeout) {
        WebSocketAdmin wsAdmin = WebSocketAdmin.get(ctx);
        if (wsAdmin == null) {
            return true;//timeout
        }
        int newIdleConnectTimeout = wsAdmin.getWebSocketClient()
                                           .addAndGetIdleConnectTimeout(idleConnectTimeout);
        if (newIdleConnectTimeout >= WebSocketConfigReader.getIdleConnectTimeout()) {
            WebSocketAdmin.close(ctx);
            return true;
        }
        return false;//connect
    }

}
