package com.polaris.container.gateway.proxy.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.container.gateway.pojo.HttpProtocolWebSocket;
import com.polaris.container.gateway.pojo.HttpRequestWrapper;
import com.polaris.container.gateway.proxy.HostResolver;
import com.polaris.container.gateway.proxy.HttpFilters;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;

public class WebSocketHandlerDefaultImpl implements WebSocketHandler {
    
    private static final Logger log = LoggerFactory.getLogger(WebSocketHandlerDefaultImpl.class);
    
    /**
     * 处理http请求为websocket握手时升级为websocket
     * */
    @Override
    public void upgrade(
            HttpRequestWrapper req, 
            ChannelHandlerContext ctx, 
            HostResolver hostResolver, 
            EventLoopGroup eventLoopGroup,
            HttpFilters filters) {
        log.debug("websocket upgrade...");
        
        //get host and port
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
                req.uri(), null, false);
        
        //handshaker
        WebSocketServerHandshaker handshaker = wsFactory.newHandshaker(req);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            
            //http filter
            HttpResponse response = filters.clientToProxyRequest(WebSocketSupport.convertParameter(req));
            if (response != null) {
                
                //filter failed;
                ctx.writeAndFlush(response,ctx.channel().newPromise());
                
            } else {
                //connect remote websocket
                if (!WebSocketSupport.createWSClient(req, ctx, hostResolver,eventLoopGroup, handshaker)) {
                    
                    //failed
                    WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
                }
            }
        }
        return;
    }
    
    @Override
    public void proxyToServer(ChannelHandlerContext ctx, WebSocketFrame frame) {
        
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

    @Override
    public boolean isWSProtocol(HttpRequestWrapper req) {
        return WebSocketSupport.isWSProtocol(req);
    }
    
    @Override
    public boolean userEventTriggered(ChannelHandlerContext ctx,int idleConnectTimeout) {
        WebSocketAdmin wsAdmin = WebSocketAdmin.get(ctx);
        if (wsAdmin == null) {
            return true;//timeout
        }
        int newIdleConnectTimeout = wsAdmin.getWebSocketClient()
                                           .addAndGetIdleConnectTimeout(idleConnectTimeout);
        if (newIdleConnectTimeout >= HttpProtocolWebSocket.getIdleConnectTimeout()) {
            WebSocketAdmin.close(ctx);
            return true;
        }
        return false;//connect
    }

}
