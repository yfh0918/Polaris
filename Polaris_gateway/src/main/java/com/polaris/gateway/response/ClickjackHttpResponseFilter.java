package com.polaris.gateway.response;

import org.springframework.stereotype.Service;

import com.polaris.gateway.GatewayConstant;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

/**
 * @author:Winning
 *
 * Description:
 *
 */
@Service
public class ClickjackHttpResponseFilter extends HttpResponseFilter {
	
    @Override
    public HttpResponse doFilter(HttpRequest originalRequest, HttpResponse httpResponse) {
        httpResponse.headers().add("X-FRAME-OPTIONS", GatewayConstant.X_Frame_Option);
        return httpResponse;
    }
}
