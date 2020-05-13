package com.polaris.container.gateway.response;

import org.apache.commons.lang3.tuple.ImmutablePair;

import com.polaris.container.gateway.HttpFilterChain;
import com.polaris.container.gateway.HttpMessage;

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

    public ImmutablePair<Boolean, HttpMessage> doFilter(HttpRequest originalRequest, HttpResponse httpResponse) {
    	HttpMessage httpMessage = new HttpMessage();
    	for (HttpResponseFilter filter : filters) {
        	if (!skip(filter)) {
                boolean result = filter.doFilter(originalRequest, httpResponse, httpMessage);
                if (result) {
                	return new ImmutablePair<>(true, httpMessage);
                }
        	}
        }
        return new ImmutablePair<>(false, null);
    }
    
    
}
