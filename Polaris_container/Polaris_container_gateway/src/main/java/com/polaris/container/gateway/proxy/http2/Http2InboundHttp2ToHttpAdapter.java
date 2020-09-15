package com.polaris.container.gateway.proxy.http2;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.handler.codec.http.DefaultLastHttpContent;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http2.Http2Error;
import io.netty.handler.codec.http2.Http2EventAdapter;
import io.netty.handler.codec.http2.Http2Exception;
import io.netty.handler.codec.http2.Http2Headers;
import io.netty.handler.codec.http2.Http2Settings;
import io.netty.handler.codec.http2.Http2Stream;
import io.netty.handler.codec.http2.HttpConversionUtil;
import io.netty.util.ReferenceCountUtil;

public class Http2InboundHttp2ToHttpAdapter extends Http2EventAdapter{
    
    private final boolean validateHttpHeaders;
    private final boolean propagateSettings;
    
    protected Http2InboundHttp2ToHttpAdapter(boolean validateHttpHeaders, boolean propagateSettings) {
        this.validateHttpHeaders = validateHttpHeaders;
        this.propagateSettings = propagateSettings;
    }
    
    @Override
    public int onDataRead(ChannelHandlerContext ctx, int streamId, ByteBuf data, int padding, boolean endOfStream)
                    throws Http2Exception {
        final int dataReadableBytes = data.readableBytes();
        fireChannelRead(ctx, createMessage(data));
        if (endOfStream) {
            fireChannelRead(ctx, createMessage());
        }
        return dataReadableBytes + padding;
    }

    @Override
    public void onHeadersRead(ChannelHandlerContext ctx, int streamId, Http2Headers headers, int padding,
                    boolean endOfStream) throws Http2Exception {
        HttpRequest httpRequest = HttpConversionUtil.toHttpRequest(streamId, headers,validateHttpHeaders);
        fireChannelRead(ctx,httpRequest);
        if (endOfStream) {
            fireChannelRead(ctx, createMessage());
        }
    }

    @Override
    public void onHeadersRead(ChannelHandlerContext ctx, int streamId, Http2Headers headers, int streamDependency,
                    short weight, boolean exclusive, int padding, boolean endOfStream) throws Http2Exception {
        onHeadersRead(ctx, streamId, headers, padding, endOfStream);
    }
    
    @Override
    public void onPushPromiseRead(ChannelHandlerContext ctx, int streamId, int promisedStreamId,
            Http2Headers headers, int padding) throws Http2Exception {
    }
    
    @Override
    public void onStreamRemoved(Http2Stream stream) {
    }
    
    @Override
    public void onRstStreamRead(ChannelHandlerContext ctx, int streamId, long errorCode) throws Http2Exception {
        ctx.fireExceptionCaught(Http2Exception.streamError(streamId, Http2Error.valueOf(errorCode),
                "HTTP/2 to HTTP layer caught stream reset"));
    }
    
    @Override
    public void onSettingsRead(ChannelHandlerContext ctx, Http2Settings settings) throws Http2Exception {
        if (propagateSettings) {
            // Provide an interface for non-listeners to capture settings
            ctx.fireChannelRead(settings);
        }
    }

    private void fireChannelRead(ChannelHandlerContext ctx, HttpObject message) {
        ctx.fireChannelRead(ReferenceCountUtil.retain(message));
    }
    private DefaultHttpContent createMessage() {
        return createMessage(null);
    }
    private DefaultHttpContent createMessage(ByteBuf data) {
        if (data == null) {
            return new DefaultLastHttpContent();
        }
        return new DefaultHttpContent(data);
    }
    

}
