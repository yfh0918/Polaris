package com.polaris.container.gateway.proxy.http2;

import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelHandler.Sharable;

@Sharable
public class Http2ChannelInboundHandlerAdapter extends ChannelInboundHandlerAdapter {

    public static Http2ChannelInboundHandlerAdapter INSTANCE = new Http2ChannelInboundHandlerAdapter();
    private Http2ChannelInboundHandlerAdapter() {}
}
