package com.polaris.container.gateway.response;

import org.apache.commons.lang3.tuple.ImmutablePair;

import com.polaris.container.gateway.HttpFilterChain;
import com.polaris.container.gateway.pojo.HttpFilterMessage;

import io.netty.handler.codec.http.HttpObject;
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

    public ImmutablePair<Boolean, HttpFilterMessage> doFilter(HttpResponse httpResponse, HttpObject httpObject) {
    	HttpFilterMessage httpMessage = new HttpFilterMessage();
    	for (HttpResponseFilter filter : filters) {
        	if (!skip(filter)) {
                boolean result = filter.doFilter(httpResponse, httpObject, httpMessage);
                if (result) {
                	return new ImmutablePair<>(true, httpMessage);
                }
        	}
        }
        return new ImmutablePair<>(false, null);
    }
    
    
}
