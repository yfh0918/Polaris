package com.polaris.demo.gateway.request;

import com.polaris.container.gateway.pojo.HttpMessage;
import com.polaris.container.gateway.request.HttpRequestFilter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;

/**
 * @author:Tom.Yu <p>
 * Description:
 * <p>
 * Token拦截
 */
public class TokenExtendHttpRequestFilter extends HttpRequestFilter {

	@Override
    public boolean doFilter(HttpRequest originalRequest, HttpObject httpObject, HttpMessage httpMessage, ChannelHandlerContext channelHandlerContext) {
    	//this.setResultDto(HttpRequestFilterSupport.createResultDto(Constant.TOKEN_FAIL_CODE,"TokenExtendHttpRequestFilter is added"));
        return false;
    }
}

