package com.polaris.container.gateway.request;

import org.apache.commons.lang3.tuple.ImmutablePair;

import com.polaris.container.gateway.HttpFilterChain;
import com.polaris.container.gateway.pojo.HttpFilterMessage;

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


    public ImmutablePair<Boolean, HttpFilterMessage> doFilter(HttpRequest originalRequest, HttpObject httpObject) {
    	HttpFilterMessage httpMessage = new HttpFilterMessage();
    	for (HttpRequestFilter filter : filters) {
        	if (!skip(filter)) {
                boolean result = filter.doFilter(originalRequest, httpObject, httpMessage);
                
                //不走hostResolver
                if (!httpMessage.isRunHostResolver()) {
                	return new ImmutablePair<>(true, httpMessage);
                }
                
                //走hostResolver-default
                if (result && filter.isBlacklist()) {
                    return new ImmutablePair<>(true, httpMessage);
                } else if (result && !filter.isBlacklist()) {
                    break;
                }
        	}
        }
        return new ImmutablePair<>(false, null);
    }

}
