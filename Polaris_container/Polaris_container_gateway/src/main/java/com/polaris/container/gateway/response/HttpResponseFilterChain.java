package com.polaris.container.gateway.response;

import org.apache.commons.lang3.tuple.ImmutablePair;

import com.polaris.container.gateway.HttpFilterChain;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

/**
 * @author:Tom.Yu
 *
 * Description:
 *
 */
public class HttpResponseFilterChain extends HttpFilterChain<HttpResponseFilter> {
    
    public static HttpResponseFilterChain INSTANCE = new HttpResponseFilterChain();
    private HttpResponseFilterChain() {}

    public ImmutablePair<Boolean, HttpResponseFilter> doFilter(HttpRequest originalRequest, HttpResponse httpResponse) {
        for (HttpResponseFilter filter : filters) {
        	if (!skip(filter)) {
                boolean result = filter.doFilter(originalRequest, httpResponse);
                if (result) {
                	return new ImmutablePair<>(true, filter);
                }
        	}
        }
        return new ImmutablePair<>(false, null);
    }
    
    
}
