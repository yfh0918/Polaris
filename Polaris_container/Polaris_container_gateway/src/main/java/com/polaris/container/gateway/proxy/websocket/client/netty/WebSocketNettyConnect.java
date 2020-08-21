package com.polaris.container.gateway.proxy.websocket.client.netty;

import java.net.URI;

import com.polaris.container.gateway.proxy.websocket.client.WebSocketClientListener;
import com.polaris.container.gateway.proxy.websocket.client.WebSocketException;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;

public class WebSocketNettyConnect {

    private Channel channelClient;
    private WebSocketNettyClientHandler handler;
    private WebSocketNettyConnect() {}
    private void connect(String url, WebSocketClientListener clientListener) {
        try {
            URI uri = new URI(url);
            final int port = uri.getPort();
            handler = new WebSocketNettyClientHandler(
                    WebSocketClientHandshakerFactory.newHandshaker(uri, WebSocketVersion.V13, null, false, new DefaultHttpHeaders()), 
                    clientListener);

            Bootstrap boot = new Bootstrap();
            boot.group(WebSocketNettyGroup.getGroup()).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) {
                    ChannelPipeline p = ch.pipeline();
                    p.addLast(new HttpClientCodec(), new HttpObjectAggregator(8192), handler);
                }
            });
            channelClient = boot.connect(uri.getHost(), port).sync().channel();
            handler.handshakeFuture().sync();
            channelClient.closeFuture().addListener((r) -> {
                WebSocketNettyGroup.shutdown();
            });
        } catch (Exception e) {
            throw new WebSocketException(e);
        }
    }
    
    public static WebSocketNettyConnect getConnect(String url,WebSocketClientListener clientListener) {
        WebSocketNettyConnect connect = new WebSocketNettyConnect();
        connect.connect(url, clientListener);
        return connect;
    }

    public Channel getChannelClient() {
        return channelClient;
    }

    public void setChannelClient(Channel channelClient) {
        this.channelClient = channelClient;
    }

    public WebSocketNettyClientHandler getHandler() {
        return handler;
    }

    public void setHandler(WebSocketNettyClientHandler handler) {
        this.handler = handler;
    }
}
