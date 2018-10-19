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
 * @author:Tom.Yu
 *
 * Description:
 *
 * IP黑名单拦截
 */
@Service
public class IpHttpRequestFilter extends HttpRequestFilter {
	private static LogUtil logger = LogUtil.getInstance(IpHttpRequestFilter.class);

    @Override
    public boolean doFilter(HttpRequest originalRequest, HttpObject httpObject, ChannelHandlerContext channelHandlerContext) {
        if (httpObject instanceof HttpRequest) {
            logger.debug("filter:{}", this.getClass().getName());
            HttpRequest httpRequest = (HttpRequest) httpObject;
            String realIp = GatewayConstant.getRealIp(httpRequest);

            for (Pattern pat : ConfUtil.getPattern(FilterType.IP.name())) {
                Matcher matcher = pat.matcher(realIp);
                if (matcher.find()) {
                    hackLog(logger, GatewayConstant.getRealIp(httpRequest), FilterType.IP.name(), pat.toString());
                    return true;
                }
            }
        }
        return false;
    }
}
