package com.polaris.container.gateway.proxy.http2;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpServerUpgradeHandler.UpgradeEvent;
import io.netty.util.ReferenceCountUtil;

@Sharable
public class Http2EventTriggerHandler extends SimpleChannelInboundHandler<Object>{
    
    public static String NAME = "Http2EventTriggerHandler";
    
    public static Http2EventTriggerHandler INSTANCE = new Http2EventTriggerHandler();
    
    private Http2EventTriggerHandler() {}
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        ctx.pipeline().remove(this);
        ctx.fireChannelRead(ReferenceCountUtil.retain(msg));
    }
    
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        
        // for h2
        if (evt instanceof UpgradeEvent) {
            try {
                ctx.pipeline().addAfter(Http2EventTriggerHandler.NAME, Http2SettingsHandler.NAME, Http2SettingsHandler.INSTANCE);
                ctx.pipeline().addAfter(Http2SettingsHandler.NAME, Http2EmptyHandler.NAME, Http2EmptyHandler.INSTANCE);
            } catch (Throwable cause) {
                exceptionCaught(ctx, cause);
            } finally {
                ctx.pipeline().remove(this);
            }
        } 
        ctx.fireUserEventTriggered(evt);
    }
}
