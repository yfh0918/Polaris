package com.polaris.container.gateway.proxy;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.LastHttpContent;

import java.net.InetSocketAddress;

/**
 * Convenience base class for implementations of {@link HttpFilters}.
 */
public class HttpFiltersAdapter implements HttpFilters {
    /**
     * A default, stateless, no-op {@link HttpFilters} instance.
     */
    public static final HttpFiltersAdapter NOOP_FILTER = new HttpFiltersAdapter(null);

    protected final HttpRequest originalRequest;
    protected final ChannelHandlerContext ctx;

    public HttpFiltersAdapter(HttpRequest originalRequest,
            ChannelHandlerContext ctx) {
        this.originalRequest = originalRequest;
        this.ctx = ctx;
    }

    public HttpFiltersAdapter(HttpRequest originalRequest) {
        this(originalRequest, null);
    }

    @Override
    public HttpResponse clientToProxyRequest(HttpObject httpObject) {
        return null;
    }

    @Override
    public HttpResponse proxyToServerRequest(HttpObject httpObject) {
        return null;
    }

    @Override
    public void proxyToServerRequestSending(FullFlowContext flowContext,HttpRequest httpRequest) {
    }

    @Override
    public void proxyToServerRequestSent(FullFlowContext flowContext, LastHttpContent lastHttpContent) {
    }

    @Override
    public HttpObject serverToProxyResponse(HttpObject httpObject) {
        return httpObject;
    }

    @Override
    public void serverToProxyResponseTimedOut() {
    }

    @Override
    public void serverToProxyResponseReceiving(FullFlowContext flowContext) {
    }

    @Override
    public void serverToProxyResponseReceived(FullFlowContext flowContext) {
    }

    @Override
    public HttpObject proxyToClientResponse(HttpObject httpObject) {
        return httpObject;
    }

    @Override
    public void proxyToServerConnectionQueued(FullFlowContext flowContext) {
    }

    @Override
    public InetSocketAddress proxyToServerResolutionStarted(FullFlowContext flowContext,
            String resolvingServerHostAndPort) {
        return null;
    }

    @Override
    public void proxyToServerResolutionFailed(FullFlowContext flowContext, String hostAndPort) {
    }

    @Override
    public void proxyToServerResolutionSucceeded(FullFlowContext flowContext,String serverHostAndPort,
            InetSocketAddress resolvedRemoteAddress) {
    }

    @Override
    public void proxyToServerConnectionStarted(FullFlowContext flowContext) {
    }

    @Override
    public void proxyToServerConnectionSSLHandshakeStarted(FullFlowContext flowContext) {
    }

    @Override
    public void proxyToServerConnectionFailed(FullFlowContext flowContext) {
    }

    @Override
    public void proxyToServerConnectionSucceeded(FullFlowContext flowContext) {
    }
}
