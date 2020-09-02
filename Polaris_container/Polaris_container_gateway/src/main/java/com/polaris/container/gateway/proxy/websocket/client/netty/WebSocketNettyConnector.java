package com.polaris.container.gateway.proxy.websocket.client.netty;

import java.net.URI;

import com.polaris.container.gateway.proxy.websocket.WebSocketException;
import com.polaris.container.gateway.proxy.websocket.client.WebSocketClientListener;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;

public class WebSocketNettyConnector {

    private WebSocketNettyConnector() {}
    private Channel connect(String url, EventLoopGroup eventLoopGroup, WebSocketClientListener clientListener) {
        try {
            URI uri = new URI(url);
            final int port = uri.getPort();
            WebSocketNettyClientHandler handler = new WebSocketNettyClientHandler(
                    WebSocketClientHandshakerFactory.newHandshaker(uri, WebSocketVersion.V13, null, false, new DefaultHttpHeaders()), 
                    clientListener);
            Channel channelClient = new Bootstrap()
                    .group(eventLoopGroup).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new HttpClientCodec(), new HttpObjectAggregator(Integer.MAX_VALUE), handler);
                        }
                    })
                    .connect(uri.getHost(), port).sync().channel();
            handler.handshakeFuture().sync();
            return channelClient;
        } catch (Exception e) {
            e.printStackTrace();
            throw new WebSocketException(e.getMessage(),e);
        }
    }
    
    public static Channel getChannel(String url,EventLoopGroup eventLoopGroup,WebSocketClientListener clientListener) {
        WebSocketNettyConnector connector = new WebSocketNettyConnector();
        return connector.connect(url, eventLoopGroup, clientListener);
    }
}
