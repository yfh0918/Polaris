package com.polaris.demo.gateway.response;

import org.springframework.stereotype.Service;

import com.polaris.container.gateway.HttpFilterEnum;
import com.polaris.container.gateway.response.HttpResponseFilter;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

/**
 * @author:Tom.Yu
 *
 * Description:
 *
 */
@Service
public class TokenHttpResponseFilter extends HttpResponseFilter {
	
	static {
		//注册扩展过滤器
		HttpFilterEnum.addExtendFilter("gateway.response.token", TokenHttpResponseFilter.class, 2);
    }
	
    @Override
    public boolean doFilter(HttpRequest originalRequest, HttpResponse httpResponse) {
        
        return false;
    }
}
