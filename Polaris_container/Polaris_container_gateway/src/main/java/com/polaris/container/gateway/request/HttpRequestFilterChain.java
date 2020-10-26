package com.polaris.container.gateway.request;

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


    public HttpFilterMessage doFilter(HttpRequest httpRequest, HttpObject httpObject) {
    	for (HttpRequestFilter filter : filters) {
        	if (!skip(filter)) {
        	    HttpFilterMessage httpMessage = filter.doFilter(httpRequest, httpObject);
        	    if (httpMessage != null) {
        	        if (filter.isBlacklist()) {
        	            return httpMessage;//black list
        	        } else {
        	            return null;
        	        }
        	    }
        	}
        }
        return null;
    }

}
