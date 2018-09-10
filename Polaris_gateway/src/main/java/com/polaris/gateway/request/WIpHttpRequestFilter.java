package com.polaris.gateway.request;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.polaris.gateway.GatewayConstant;
import com.polaris.gateway.util.ConfUtil;
import com.polaris.comm.util.LogUtil;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;

/**
 * @author:Winning
 *
 * Description:
 *
 * IP白名单拦截
 */
@Service
public class WIpHttpRequestFilter extends HttpRequestFilter {
	private static LogUtil logger = LogUtil.getInstance(WIpHttpRequestFilter.class);

    @Override
    public boolean isBlacklist() {
        return false;
    }

    @Override
    public boolean doFilter(HttpRequest originalRequest, HttpObject httpObject, ChannelHandlerContext channelHandlerContext) {
        if (httpObject instanceof HttpRequest) {
            logger.debug("filter:{}", this.getClass().getName());
            HttpRequest httpRequest = (HttpRequest) httpObject;
            for (Pattern pat : ConfUtil.getPattern(FilterType.WIP.name())) {
                Matcher matcher = pat.matcher(GatewayConstant.getRealIp(httpRequest, channelHandlerContext));
                if (matcher.find()) {
                    hackLog(logger, GatewayConstant.getRealIp(httpRequest, channelHandlerContext), FilterType.WIP.name(), pat.toString());
                    return true;
                }
            }
        }
        return false;
    }
}
