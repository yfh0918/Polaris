package com.polaris.container.gateway.proxy.websocket.client.netty;

import com.polaris.container.gateway.proxy.websocket.WebSocketAdmin;
import com.polaris.container.gateway.proxy.websocket.WebSocketConfigReader;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

public class WebSocketNettyGroup {
    private volatile static EventLoopGroup group = null;
    public static EventLoopGroup getGroup() {
        if (group == null) {
            synchronized(EventLoopGroup.class) {
                if (group == null) {
                    if (WebSocketConfigReader.getGroupThreadNumber() > 0) {
                        group = new NioEventLoopGroup(WebSocketConfigReader.getGroupThreadNumber());
                    } else {
                        group = new NioEventLoopGroup();
                    }
                }
            }
        }
        return group;
    }
    public static void shutdown() {
        if (WebSocketAdmin.size() < 1) {
            synchronized(EventLoopGroup.class) {
                if (group != null) {
                    group.shutdownGracefully();
                    group = null;
                }
            }
            
        }
    }
}
