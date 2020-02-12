package com.polaris.container.gateway.response;

import java.util.List;

import org.springframework.stereotype.Service;

import com.polaris.container.gateway.GatewayConstant;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

/**
 * @author:Tom.Yu
 *
 * Description:
 *
 */
@Service
public class ClickjackHttpResponseFilter extends HttpResponseFilter {
	
    @Override
    public boolean doFilter(HttpRequest originalRequest, HttpResponse httpResponse) {
        httpResponse.headers().add("X-FRAME-OPTIONS", GatewayConstant.X_Frame_Option);
        List<String> originHeader = GatewayConstant.getHeaderValues(originalRequest, "Origin");
        if (originHeader.size() > 0) {
        	httpResponse.headers().add("Access-Control-Allow-Credentials", "true");
        	httpResponse.headers().add("Access-Control-Allow-Origin", originHeader.get(0));
        }
        return false;
    }
}
