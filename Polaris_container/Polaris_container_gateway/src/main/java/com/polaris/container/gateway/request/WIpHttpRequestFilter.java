package com.polaris.container.gateway.request;

import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.polaris.container.gateway.GatewayConstant;
import com.polaris.container.gateway.util.ConfUtil;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;

/**
 * @author:Tom.Yu
 *
 * Description:
 *
 * IP白名单拦截
 */
@Service
public class WIpHttpRequestFilter extends HttpRequestFilter {
	private static Logger logger = LoggerFactory.getLogger(WIpHttpRequestFilter.class);

    @Override
    public boolean isBlacklist() {
        return false;
    }

    @Override
    public boolean doFilter(HttpRequest originalRequest, HttpObject httpObject, ChannelHandlerContext channelHandlerContext) {
        if (httpObject instanceof HttpRequest) {
            logger.debug("filter:{}", this.getClass().getName());
            HttpRequest httpRequest = (HttpRequest) httpObject;
            String ip = GatewayConstant.getRealIp(httpRequest);
            Pattern pat = ConfUtil.getPattern(FilterType.WIP.name(),ip);
            if (pat != null) {
                hackLog(logger, GatewayConstant.getRealIp(httpRequest), FilterType.WIP.name(), pat.toString());
            	return true;
            }
        }
        return false;
    }
}
