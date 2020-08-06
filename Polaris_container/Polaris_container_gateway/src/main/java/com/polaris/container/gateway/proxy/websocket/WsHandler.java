package com.polaris.container.gateway.proxy.websocket;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.enums.ReadyState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.common.net.HostAndPort;
import com.polaris.container.gateway.pojo.HttpHostContext;
import com.polaris.container.gateway.proxy.HostResolver;
import com.polaris.container.gateway.proxy.HttpFilters;
import com.polaris.core.pojo.ServerHost;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
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

@Component
public class WsHandler {
    
    private static final Logger log = LoggerFactory.getLogger(WsHandler.class);
    
    /**
     * 处理http请求为websocket握手时升级为websocket
     * */
    public boolean upgrade(
            HttpRequest req, 
            ChannelHandlerContext ctx, 
            HostResolver hostResolver, 
            HttpFilters filters, 
            String serverHostAndPort) {
        boolean flag = false;
        log.debug("websocket 请求接入");
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
                req.uri(), null, false);
        WebSocketServerHandshaker handshaker = wsFactory.newHandshaker(req);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            
            //建立http连接需要filter
            HttpResponse response = filters.clientToProxyRequest(req);
            if (response != null) {
                ctx.writeAndFlush(response,ctx.channel().newPromise());
            } else {
                //与远程的websocket建立连接
                if (connectToRemoteWs(req, ctx, hostResolver,serverHostAndPort)) {
                    //本机websocket建联
                    handshaker.handshake(ctx.channel(), req);
                    WsConstant.wsHandshakerMap.put(req.uri(), handshaker);
                    WsConstant.wsCtx.put(req.uri(), ctx);
                    WsConstant.ctxWs.put(ctx, handshaker);
                    flag = true;
                } else {
                    WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
                }  
            }
        }
        return flag;
    }

    // 处理Websocket的代码
    public void handle(ChannelHandlerContext ctx, WebSocketFrame frame) {
        
        //close
        if (frame instanceof CloseWebSocketFrame) {
            //先关闭远程websocket的连接
            WebSocketClient myWebsocketClient = WsConstant.wsCtxClient.get(ctx);
            if (myWebsocketClient != null) {
                myWebsocketClient.close();
            }
            WsConstant.ctxWs.get(ctx).close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            return;
        }
        
        // ping
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            WebSocketClient myWebsocketClient = WsConstant.wsCtxClient.get(ctx);
            if (myWebsocketClient != null) {
                myWebsocketClient.sendPing();
            } else {
                WsConstant.ctxWs.get(ctx).close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            }
            return;
        }
        
        // text
        if (frame instanceof TextWebSocketFrame) {
            String request = ((TextWebSocketFrame) frame).text();
            WebSocketClient myWebsocketClient = WsConstant.wsCtxClient.get(ctx);
            if (myWebsocketClient != null) {
                myWebsocketClient.send(request);
            } else {
                WsConstant.ctxWs.get(ctx).close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            }
            return;
        }
        
        //byte
        if (frame instanceof BinaryWebSocketFrame) {
            BinaryWebSocketFrame binaryWebSocketFrame = (BinaryWebSocketFrame) frame;
            ByteBuf content = binaryWebSocketFrame.content();
            final int length = content.readableBytes();
            final byte[] array = new byte[length];
            content.getBytes(content.readerIndex(), array, 0, length);
            WebSocketClient myWebsocketClient = WsConstant.wsCtxClient.get(ctx);
            if (myWebsocketClient != null) {
                myWebsocketClient.send(array);
            } else {
                WsConstant.ctxWs.get(ctx).close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            }
            return;
        }
    }

    /**
     * 与远程websocket建立连接
     * */
    private boolean connectToRemoteWs(HttpRequest req, 
            ChannelHandlerContext ctx, 
            HostResolver hostResolver, 
            String serverHostAndPort) {
        boolean flag = false;
        try {
            //context
            String contextPath = HttpHostContext.getContextPath(req.uri());
            HostAndPort parsedHostAndPort = HostAndPort.fromString(serverHostAndPort);
            InetSocketAddress address = hostResolver.resolve(parsedHostAndPort.getHost(), parsedHostAndPort.getPortOrDefault(80), contextPath);
            String websocketStr = ServerHost.HTTP_PREFIX +address.getHostName() + ":" + address.getPort() + contextPath;
            WebSocketClient client = new WebsocketClientImpl(websocketStr, ctx);
            client.connect();
            for (int i = 0; i < 10 ; i++) {
                if (client.getReadyState().equals(ReadyState.OPEN)) {
                    flag = true;
                    WsConstant.wsClientCtx.put(client, ctx);
                    WsConstant.wsCtxClient.put(ctx, client);
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

}
