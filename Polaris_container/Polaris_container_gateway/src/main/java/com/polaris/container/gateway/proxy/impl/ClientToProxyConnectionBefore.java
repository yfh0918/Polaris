/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.polaris.container.gateway.proxy.impl;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN;
import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_MODIFIED;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_0;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import com.google.common.net.HostAndPort;
import com.polaris.container.gateway.HttpConstant;
import com.polaris.container.gateway.pojo.HttpHtml;
import com.polaris.container.gateway.pojo.HttpProtocol;
import com.polaris.container.gateway.pojo.HttpProxy;
import com.polaris.container.gateway.pojo.HttpRequestWrapper;
import com.polaris.core.util.FileUtil;
import com.polaris.core.util.StringUtil;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http2.HttpConversionUtil;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

public class ClientToProxyConnectionBefore extends SimpleChannelInboundHandler<HttpRequest> {
    public static final String NAME = ClientToProxyConnectionBefore.class.getSimpleName();
    public static final String HTTP_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";
    public static final String HTTP_DATE_GMT_TIMEZONE = "GMT";
    public static final int HTTP_CACHE_SECONDS = 60;

    private HttpRequestWrapper request;

    @Override
    public void channelRead0(ChannelHandlerContext ctx, HttpRequest request) throws Exception {
        if (!request.decoderResult().isSuccess()) {
            pageError(ctx,"request decoderResult is failed");
            return;
        }
        
        //parameter convert
        String serverHostAndPort = HttpProtocol.identifyHostAndPort(request);
        HostAndPort hostAndPort = HttpProtocol.getHostAndPort(serverHostAndPort);
        String host = hostAndPort.getHost();

        HttpProxy httpProxy = null;
        HttpProxy defaultHttpProxy = null;
        List<HttpProxy> proxyList = HttpProxy.getServerNameMap().get(host);
        if (proxyList != null) {
            for (HttpProxy proxy : proxyList) {
                if (proxy.getContext().equals(HttpConstant.SLASH)) {
                    defaultHttpProxy = proxy;
                    continue;
                }
                if (request.uri().startsWith(proxy.getContext())) {
                    httpProxy = proxy;
                    break;
                }
            }
        }
        if (httpProxy == null) {
            if (defaultHttpProxy == null) {
                pageError(ctx, "host:"+host+",uri:"+request.uri() + " is not found");
                return;
            }
            httpProxy = defaultHttpProxy;
        }
        
        //wrapper request
        HttpRequestWrapper requestWrapper = new HttpRequestWrapper(request);
        requestWrapper.setContext(httpProxy.getContext());
        requestWrapper.setHostAndPort(hostAndPort);
        requestWrapper.setHttpProxy(httpProxy);
        requestWrapper.setOrgUri(request.uri());
        requestWrapper.setServerHostAndPort(serverHostAndPort);
        request.setUri((requestWrapper.getOrgUri().replaceFirst(httpProxy.getContext(), httpProxy.getRewrite())).replace("//", "/")); 

        //is it html file?
        this.request = requestWrapper;
        if (StringUtil.isNotEmpty(httpProxy.getRoot()) && StringUtil.isNotEmpty(httpProxy.getIndex())) {
            channelReadHtml(ctx);
        } else {
            channelReadApi(ctx);
        }
    }
    
    private void channelReadApi(ChannelHandlerContext ctx) {
        ctx.fireChannelRead(ReferenceCountUtil.retain(request)); 
    }
    
    private void channelReadHtml(ChannelHandlerContext ctx) throws Exception {
        
        //if it is html's file must be got from client
        if (!GET.equals(request.method())) {
            pageError(ctx, "host:"+request.getHostAndPort().getHost()+",uri:"+request.uri() + " method is not get method");
            return;
        }

        final String path = sanitizeUri(request.getHttpProxy());
        if (path == null) {
            page404(ctx);
            return;
        }

        File file = new File(path);
        if (file.isHidden()) {
            page404(ctx);
            return;
        }

        if (file.isDirectory()) {
            page404(ctx);
            return;
        }

        if (!file.isFile()) {
            page404(ctx);
            return;
        }

        //response html
        responseHtml(ctx, file);
    }
    
    private void responseHtml(ChannelHandlerContext ctx, File file) throws Exception {
        // Cache Validation
        String ifModifiedSince = request.headers().get(HttpHeaderNames.IF_MODIFIED_SINCE);
        if (ifModifiedSince != null && !ifModifiedSince.isEmpty()) {
            SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
            Date ifModifiedSinceDate = dateFormatter.parse(ifModifiedSince);

            // Only compare up to the second because the datetime format we send to the client
            // does not have milliseconds
            long ifModifiedSinceDateSeconds = ifModifiedSinceDate.getTime() / 1000;
            long fileLastModifiedSeconds = file.lastModified() / 1000;
            if (ifModifiedSinceDateSeconds == fileLastModifiedSeconds) {
                this.sendNotModified(ctx);
                return;
            }
        }

        RandomAccessFile raf = new RandomAccessFile(file, "r");
        long fileLength = raf.length();
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK,Unpooled.wrappedBuffer(toByteBuffer(raf)));
        HttpUtil.setContentLength(response, fileLength);
        setContentTypeHeader(response, file);
        setDateAndCacheHeaders(response, file);
        final boolean keepAlive = HttpUtil.isKeepAlive(request);
        if (!keepAlive) {
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
        } else if (request.protocolVersion().equals(HTTP_1_0)) {
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }
        ChannelFuture sendFileFuture = ctx.writeAndFlush(wrapperHttp2(response));

        // Decide whether to close the connection or not.
        if (!keepAlive) {
            // Close the connection when the whole content is written out.
            sendFileFuture.addListener(ChannelFutureListener.CLOSE);
        }
    }
    
    private void pageError(ChannelHandlerContext ctx, String message) throws Exception {
        if (request == null) {
            this.sendError(ctx, FORBIDDEN, message);
            return;
        }
        String errorPage = request.getHttpProxy().getError();
        if (StringUtil.isNotEmpty(errorPage)) {
            File file = new File(getFilePath(errorPage));
            if (file.isFile()) {
                responseHtml(ctx, file);
                return;
            }
        } 
        this.sendError(ctx, FORBIDDEN, message);
    }
    
    private void page404(ChannelHandlerContext ctx) throws Exception {
        if (request == null) {
            this.sendError(ctx, NOT_FOUND,NOT_FOUND.toString());
            return;
        }
        String page404 = request.getHttpProxy().getNotFound();
        if (StringUtil.isNotEmpty(page404)) {
            File file = new File(getFilePath(page404));
            if (file.isFile()) {
                responseHtml(ctx, file);
                return;
            }
        } 
        this.sendError(ctx, NOT_FOUND,NOT_FOUND.toString());
    }
    
    private String getFilePath(String node) {
        return  request.getHttpProxy().getRoot() + 
                File.separator + 
                request.getHttpProxy().getRewrite().replace('/', File.separatorChar) +
                File.separator + node;
    }
    
    /**
     * Mapped File way MappedByteBuffer 可以在处理大文件时，提升性能
     *
     * @param raf
     * @return
     * @throws IOException
     */
    private static ByteBuffer toByteBuffer(RandomAccessFile raf) throws IOException {
        FileChannel fc = null;
        try {
            fc = raf.getChannel();
            MappedByteBuffer byteBuffer = fc.map(MapMode.READ_ONLY, 0, fc.size()).load();
            return byteBuffer;
        } catch (IOException e) {
            throw e;
        } finally {
            try {
                fc.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        if (ctx.channel().isActive()) {
            sendError(ctx, INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR.toString());
        }
    }

    private String sanitizeUri(HttpProxy htmlObj) {
        
        // Decode the path.
        String uri = request.getUri();
        if (uri.equals(htmlObj.getRewrite())) {
            return htmlObj.getRoot() + File.separatorChar + htmlObj.getRewrite() + File.separatorChar + htmlObj.getIndex();
        }

        try {
            uri = URLDecoder.decode(uri, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new Error(e);
        }
        uri = uri.replace('/', File.separatorChar);
        return htmlObj.getRoot() + File.separatorChar + uri;
    }

    private void sendError(ChannelHandlerContext ctx, HttpResponseStatus status, String message) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HTTP_1_1, status, Unpooled.copiedBuffer("Failure: " + status + "\r\n", CharsetUtil.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
        this.sendAndCleanupConnection(ctx, response);
    }

    /**
     * When file timestamp is the same as what the browser is sending up, send a "304 Not Modified"
     *
     * @param ctx
     *            Context
     */
    private void sendNotModified(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, NOT_MODIFIED, Unpooled.EMPTY_BUFFER);
        setDateHeader(response);

        this.sendAndCleanupConnection(ctx, response);
    }

    /**
     * If Keep-Alive is disabled, attaches "Connection: close" header to the response
     * and closes the connection after the response being sent.
     */
    private void sendAndCleanupConnection(ChannelHandlerContext ctx, FullHttpResponse response) {
        final HttpRequest request = this.request;
        boolean keepAlive = false;
        try {
            keepAlive = HttpUtil.isKeepAlive(request);
        } catch (Exception ex) {}
        HttpUtil.setContentLength(response, response.content().readableBytes());
        if (!keepAlive) {
            // We're going to close the connection as soon as the response is sent,
            // so we should also make it clear for the client.
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
        } else if (request.protocolVersion().equals(HTTP_1_0)) {
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }

        ChannelFuture flushPromise = ctx.writeAndFlush(wrapperHttp2(response));

        if (!keepAlive) {
            // Close the connection as soon as the response is sent.
            flushPromise.addListener(ChannelFutureListener.CLOSE);
        }
    }

    /**
     * Sets the Date header for the HTTP response
     *
     * @param response
     *            HTTP response
     */
    private static void setDateHeader(FullHttpResponse response) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
        dateFormatter.setTimeZone(TimeZone.getTimeZone(HTTP_DATE_GMT_TIMEZONE));

        Calendar time = new GregorianCalendar();
        response.headers().set(HttpHeaderNames.DATE, dateFormatter.format(time.getTime()));
    }

    /**
     * Sets the Date and Cache headers for the HTTP Response
     *
     * @param response
     *            HTTP response
     * @param fileToCache
     *            file to extract content type
     */
    private static void setDateAndCacheHeaders(HttpResponse response, File fileToCache) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
        dateFormatter.setTimeZone(TimeZone.getTimeZone(HTTP_DATE_GMT_TIMEZONE));

        // Date header
        Calendar time = new GregorianCalendar();
        response.headers().set(HttpHeaderNames.DATE, dateFormatter.format(time.getTime()));

        // Add cache headers
        time.add(Calendar.SECOND, HTTP_CACHE_SECONDS);
        response.headers().set(HttpHeaderNames.EXPIRES, dateFormatter.format(time.getTime()));
        response.headers().set(HttpHeaderNames.CACHE_CONTROL, "private, max-age=" + HTTP_CACHE_SECONDS);
        response.headers().set(
                HttpHeaderNames.LAST_MODIFIED, dateFormatter.format(new Date(fileToCache.lastModified())));
    }

    /**
     * Sets the content type header for the HTTP Response
     *
     * @param response
     *            HTTP response
     * @param file
     *            file to extract content type
     */
    private void setContentTypeHeader(HttpResponse response, File fileToCache) {
        String contentType = HttpHtml.getHtmlSupportMap().get(FileUtil.getSuffix(fileToCache.getName()));
        if (contentType == null) {
            contentType = "applicaton/octet-stream";
        }
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, contentType);
    }

    
    private HttpResponse wrapperHttp2(HttpResponse response) {
        if (response == null) {
            return response;
        }
        try {
            String streamId = request.headers().get(HttpConversionUtil.ExtensionHeaderNames.STREAM_ID.text());
            if (streamId != null) {
                response.headers().add(HttpConversionUtil.ExtensionHeaderNames.STREAM_ID.text(),streamId);
            }
        } catch (Exception ex) {}
        return response;
    }
}
