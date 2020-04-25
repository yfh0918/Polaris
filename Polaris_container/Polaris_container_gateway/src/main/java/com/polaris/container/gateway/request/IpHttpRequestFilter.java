package com.polaris.container.gateway.request;

import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.container.gateway.GatewayConstant;
import com.polaris.container.gateway.HttpFilterHelper;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;

/**
 * @author:Tom.Yu
 *
 * Description:
 *
 * IP黑名单拦截
 */
public class IpHttpRequestFilter extends HttpRequestFilter {
	private static Logger logger = LoggerFactory.getLogger(IpHttpRequestFilter.class);

    @Override
    public boolean doFilter(HttpRequest originalRequest, HttpObject httpObject, ChannelHandlerContext channelHandlerContext) {
        if (httpObject instanceof HttpRequest) {
            logger.debug("filter:{}", this.getClass().getName());
            HttpRequest httpRequest = (HttpRequest) httpObject;
            String realIp = GatewayConstant.getRealIp(httpRequest);
            Pattern pat = HttpFilterHelper.getPattern(httpFilterEntity,realIp);
            if (pat != null) {
                hackLog(logger, GatewayConstant.getRealIp(httpRequest), IpHttpRequestFilter.class.getSimpleName(), pat.toString());
                return true;
            }
        }
        return false;
    }
}
