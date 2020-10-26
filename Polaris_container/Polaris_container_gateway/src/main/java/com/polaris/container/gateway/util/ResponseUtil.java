package com.polaris.container.gateway.util;

import java.util.Map;

import com.polaris.container.gateway.pojo.HttpFilterMessage;
import com.polaris.core.util.StringUtil;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http2.HttpConversionUtil;
import io.netty.util.CharsetUtil;

public class ResponseUtil {
    public static FullHttpResponse createResponse(HttpMessage httpMessage, HttpFilterMessage message) {
        FullHttpResponse httpResponse;
        if (StringUtil.isNotEmpty(message.getResult())) {
            ByteBuf buf = io.netty.buffer.Unpooled.copiedBuffer(message.getResult(), CharsetUtil.UTF_8); 
            httpResponse  = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, message.getStatus(), buf);
        } else {
            httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, message.getStatus());
        }
        HttpHeaders httpHeaders=new DefaultHttpHeaders();
        if (message.getHeader() != null) {
            for (Map.Entry<String, Object> entry : message.getHeader().entrySet()) {
                httpHeaders.set(entry.getKey(), entry.getValue());
            }
        }
        httpHeaders.set(HttpHeaderNames.CONTENT_TYPE, "application/json");
        httpHeaders.set(HttpHeaderNames.CONTENT_LENGTH,String.valueOf(httpResponse.content().readableBytes()));
        if (httpMessage != null) {
            String streamId = httpMessage.headers().get(HttpConversionUtil.ExtensionHeaderNames.STREAM_ID.text());
            if (streamId != null) {
                httpHeaders.set(HttpConversionUtil.ExtensionHeaderNames.STREAM_ID.text(),streamId);
            }
        }
        httpResponse.headers().add(httpHeaders);
        return httpResponse;
    }
    public static FullHttpResponse createResponse(HttpMessage httpMessage, String message, HttpResponseStatus status) {
        FullHttpResponse httpResponse;
        if (StringUtil.isNotEmpty(message)) {
            ByteBuf buf = io.netty.buffer.Unpooled.copiedBuffer(message, CharsetUtil.UTF_8); 
            httpResponse  = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, buf);
        } else {
            httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status);
        }
        HttpHeaders httpHeaders=new DefaultHttpHeaders();
        httpHeaders.set(HttpHeaderNames.CONTENT_TYPE, "application/json");
        httpHeaders.set(HttpHeaderNames.CONTENT_LENGTH,String.valueOf(httpResponse.content().readableBytes()));
        if (httpMessage != null) {
            String streamId = httpMessage.headers().get(HttpConversionUtil.ExtensionHeaderNames.STREAM_ID.text());
            if (streamId != null) {
                httpHeaders.set(HttpConversionUtil.ExtensionHeaderNames.STREAM_ID.text(),streamId);
            }
        }
        httpResponse.headers().add(httpHeaders);
        return httpResponse;
    }
}
