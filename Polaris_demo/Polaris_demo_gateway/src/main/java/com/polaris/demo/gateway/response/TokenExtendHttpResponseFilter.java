package com.polaris.demo.gateway.response;

import com.polaris.container.gateway.pojo.HttpFilterMessage;
import com.polaris.container.gateway.response.HttpResponseFilter;

import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;

/**
 * @author:Tom.Yu
 *
 * Description:
 *
 */
public class TokenExtendHttpResponseFilter extends HttpResponseFilter {
	
    @Override
    public boolean doFilter(HttpRequest originalRequest, HttpObject httpObject, HttpFilterMessage httpMessage) {
        return false;
    }
}
