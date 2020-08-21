package com.polaris.container.gateway.proxy.websocket.client.netty;

import com.polaris.container.gateway.proxy.websocket.WsAdmin;
import com.polaris.container.gateway.proxy.websocket.WsConfigReader;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

public class WebSocketNettyGroup {
    private volatile static EventLoopGroup group = null;
    public static EventLoopGroup getGroup() {
        if (group == null) {
            synchronized(EventLoopGroup.class) {
                if (group == null) {
                    if (WsConfigReader.getGroupThreadNumber() > 0) {
                        group = new NioEventLoopGroup(WsConfigReader.getGroupThreadNumber());
                    } else {
                        group = new NioEventLoopGroup();
                    }
                }
            }
        }
        return group;
    }
    public static void shutdown() {
        if (WsAdmin.size() < 1) {
            synchronized(EventLoopGroup.class) {
                if (group != null) {
                    group.shutdownGracefully();
                    group = null;
                }
            }
            
        }
    }
}
