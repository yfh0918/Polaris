package com.polaris.demo.gateway.response;

import com.polaris.container.gateway.pojo.HttpMessage;
import com.polaris.container.gateway.response.HttpResponseFilter;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

/**
 * @author:Tom.Yu
 *
 * Description:
 *
 */
public class TokenExtendHttpResponseFilter extends HttpResponseFilter {
	
    @Override
    public boolean doFilter(HttpRequest originalRequest, HttpResponse httpResponse, HttpMessage httpMessage) {
        return false;
    }
}
