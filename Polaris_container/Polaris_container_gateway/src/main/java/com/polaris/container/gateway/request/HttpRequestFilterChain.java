package com.polaris.container.gateway.request;

import org.apache.commons.lang3.tuple.ImmutablePair;

import com.polaris.container.gateway.HttpFilterChain;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;

/**
 * @author:Tom.Yu
 * <p>
 * Description:
 * <p>
 * 拦截器链
 */
public class HttpRequestFilterChain extends HttpFilterChain<HttpRequestFilter>{
    
    public static HttpRequestFilterChain INSTANCE = new HttpRequestFilterChain();
    private HttpRequestFilterChain() {}


    public ImmutablePair<Boolean, HttpRequestFilter> doFilter(HttpRequest originalRequest, HttpObject httpObject, ChannelHandlerContext channelHandlerContext) {
        for (HttpRequestFilter filter : filters) {
        	if (!skip(filter)) {
                boolean result = filter.doFilter(originalRequest, httpObject, channelHandlerContext);
                if (result && filter.isBlacklist()) {
                    return new ImmutablePair<>(true, filter);
                } else if (result && !filter.isBlacklist()) {
                    break;
                }
        	}
        }
        return new ImmutablePair<>(false, null);
    }

}
