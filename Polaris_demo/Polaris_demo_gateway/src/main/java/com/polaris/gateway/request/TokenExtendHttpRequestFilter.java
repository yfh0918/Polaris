package com.polaris.gateway.request;

import org.springframework.stereotype.Service;

import com.polaris.container.gateway.HttpFilterEnum;
import com.polaris.container.gateway.request.HttpRequestFilter;
import com.polaris.container.gateway.support.HttpRequestFilterSupport;
import com.polaris.core.Constant;

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
		HttpFilterEnum.addExtendFilter(HttpFilterEnum.Token.getKey(), TokenExtendHttpRequestFilter.class);
	}
	
	@Override
    public boolean doFilter(HttpRequest originalRequest, HttpObject httpObject, ChannelHandlerContext channelHandlerContext) {
    	this.setResultDto(HttpRequestFilterSupport.createResultDto(Constant.TOKEN_FAIL_CODE,"TokenExtendHttpRequestFilter is added"));
        return true;
    }
}

