package com.polaris.container.gateway.proxy.http2;

import io.netty.buffer.EmptyByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.ReferenceCountUtil;

public class Http2EmptyHandler extends ChannelDuplexHandler{
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof EmptyByteBuf) {
            promise.setSuccess();
            ReferenceCountUtil.release(msg);
        } else {
            ctx.write(msg, promise);
        }
    }
}
