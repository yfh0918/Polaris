package com.polaris.container.gateway.proxy;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;

/**
 * Factory for {@link HttpFilters}.
 */
public interface HttpFiltersSource {
    /**
     * Return an {@link HttpFilters} object for this request if and only if we
     * want to filter the request and/or its responses.
     * 
     * @param originalRequest
     * @return
     */
    HttpFilters filterRequest(HttpRequest originalRequest,
            ChannelHandlerContext ctx);
}
