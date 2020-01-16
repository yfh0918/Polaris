package com.polaris.gateway.request;

import org.springframework.stereotype.Service;

import com.polaris.core.Constant;
import com.polaris.gateway.HttpFilterEnum;
import com.polaris.gateway.support.HttpRequestFilterSupport;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;

/**
 * @author:Tom.Yu <p>
 * Description:
 * <p>
 * Token拦截
 */
@Service
public class TokenExtendHttpRequestFilter extends HttpRequestFilter {

	static {
		HttpFilterEnum.addExtendFilter("gateway.token", TokenExtendHttpRequestFilter.class);
	}
	
	@Override
    public boolean doFilter(HttpRequest originalRequest, HttpObject httpObject, ChannelHandlerContext channelHandlerContext) {
    	this.setResultDto(HttpRequestFilterSupport.createResultDto(Constant.TOKEN_FAIL_CODE,"TokenExtendHttpRequestFilter is added"));
        return true;
    }
}

