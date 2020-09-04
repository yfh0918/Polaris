package com.polaris.container.gateway.proxy;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;

/**
 * Convenience base class for implementations of {@link HttpFiltersSource}.
 */
public class HttpFiltersSourceAdapter implements HttpFiltersSource {

    public HttpFilters filterRequest(HttpRequest originalRequest) {
        return new HttpFiltersAdapter(originalRequest, null);
    }
    
    @Override
    public HttpFilters filterRequest(HttpRequest originalRequest,
            ChannelHandlerContext ctx) {
        return filterRequest(originalRequest);
    }
}
