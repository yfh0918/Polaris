package com.polaris.container.gateway.response;

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

    public HttpFilterMessage doFilter(HttpResponse httpResponse, HttpObject httpObject) {
    	for (HttpResponseFilter filter : filters) {
        	if (!skip(filter)) {
        	    HttpFilterMessage httpMessage = filter.doFilter(httpResponse, httpObject);
                if (httpMessage != null) {
                	return httpMessage;
                }
        	}
        }
        return null;
    }
    
    
}
