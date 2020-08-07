package com.polaris.container.gateway.proxy.websocket;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.net.HostAndPort;
import com.polaris.container.gateway.pojo.HttpHostContext;
import com.polaris.container.gateway.proxy.HostResolver;
import com.polaris.container.gateway.proxy.HttpFilters;
import com.polaris.container.gateway.proxy.websocket.client.WebSocketClientFactory;
import com.polaris.container.gateway.proxy.websocket.client.WebSocketClientInf;
import com.polaris.core.pojo.KeyValuePair;
import com.polaris.core.pojo.ServerHost;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
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

public class WsHandler {
    
    private static final Logger log = LoggerFactory.getLogger(WsHandler.class);
    
    /**
     * 处理http请求为websocket握手时升级为websocket
     * */
    public void upgrade(
            HttpRequest req, 
            ChannelHandlerContext ctx, 
            HostResolver hostResolver, 
            HttpFilters filters, 
            String serverHostAndPort) {
        log.debug("websocket 请求接入");
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
                req.uri(), null, false);
        WebSocketServerHandshaker handshaker = wsFactory.newHandshaker(req);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            
            //最大连接数
            if (WsAdmin.size() > WsConfigReader.WS_REQUEST_MAX_NMBER) {
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
                WsAdmin wsComponent = new WsAdmin();
                if (connectToRemote(req, ctx, hostResolver,serverHostAndPort,wsComponent)) {
                    //本机websocket建联
                    handshaker.handshake(ctx.channel(), req);
                    wsComponent.setUri(req.uri());
                    wsComponent.setWebSocketServerHandshaker(handshaker);
                } else {
                    WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
                }  
            }
        }
        return;
    }

    //处理Websocket的代码
    public void handle(ChannelHandlerContext ctx, WebSocketFrame frame) {
        
        //close
        if (frame instanceof CloseWebSocketFrame) {
            WsAdmin.close(ctx,(CloseWebSocketFrame) frame.retain());
            return;
        }
        
        // ping
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            WsAdmin wsComponent = WsAdmin.get(ctx);
            if (wsComponent != null) {
                wsComponent.getWebSocketClient().sendPing();
            } else {
                WsAdmin.close(ctx,(CloseWebSocketFrame) frame.retain());
            }
            return;
        }
        
        // text
        else if (frame instanceof TextWebSocketFrame) {
            WsAdmin wsComponent = WsAdmin.get(ctx);
            if (wsComponent != null) {
                wsComponent.getWebSocketClient().send(((TextWebSocketFrame) frame).text());
            } else {
                WsAdmin.close(ctx,(CloseWebSocketFrame) frame.retain());
            }
            return;
        }
        
        //byte
        else if (frame instanceof BinaryWebSocketFrame) {
            WsAdmin wsComponent = WsAdmin.get(ctx);
            if (wsComponent != null) {
                BinaryWebSocketFrame binaryWebSocketFrame = (BinaryWebSocketFrame) frame;
                ByteBuf content = binaryWebSocketFrame.content();
                final int length = content.readableBytes();
                final byte[] array = new byte[length];
                content.getBytes(content.readerIndex(), array, 0, length);
                wsComponent.getWebSocketClient().send(ByteBuffer.wrap(array));
            } else {
                
                WsAdmin.close(ctx,(CloseWebSocketFrame) frame.retain());
            }
            return;
        }
    }

    /**
     * 与远程websocket建立连接
     * */
    private boolean connectToRemote(HttpRequest req, 
            ChannelHandlerContext ctx, 
            HostResolver hostResolver, 
            String serverHostAndPort,
            WsAdmin wsComponent) {
        boolean flag = false;
        try {
            //context
            String contextPath = HttpHostContext.getContextPath(req.uri());
            HostAndPort parsedHostAndPort = HostAndPort.fromString(serverHostAndPort);
            InetSocketAddress address = hostResolver.resolve(parsedHostAndPort.getHost(), parsedHostAndPort.getPortOrDefault(80), contextPath);
            String websocketStr = ServerHost.HTTP_PREFIX +address.getHostName() + ":" + address.getPort() + req.uri();
            WebSocketClientInf client = WebSocketClientFactory.create(websocketStr, ctx);
            client.connect();
            for (int i = 0; i < 10 ; i++) {
                if (client.getState().equals(WsStatus.OPEN)) {
                    flag = true;
                    wsComponent.setChannelHandlerContext(ctx);
                    wsComponent.setWebSocketClient(client);
                    break;
                }
                TimeUnit.MILLISECONDS.sleep(200);
            }
            if (!flag) {
                client.close();
            }
        } catch (Exception e) {
            log.error("ERROR:",e);
        }
        return flag;
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
    
    public boolean isWsChannelHandlerContext(ChannelHandlerContext ctx) {
        if (WsAdmin.get(ctx) != null) {
            return true;
        }
        return false;
    }

}
