package com.polaris.gateway.request;

import java.util.List;
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
 * <p>
 * Description:
 * <p>
 * Cookie黑名单拦截
 */
@Service
public class CookieHttpRequestFilter extends HttpRequestFilter {
	private static LogUtil logger = LogUtil.getInstance(CookieHttpRequestFilter.class);

    @Override
    public boolean doFilter(HttpRequest originalRequest, HttpObject httpObject, ChannelHandlerContext channelHandlerContext) {
        if (httpObject instanceof HttpRequest) {
            logger.debug("filter:{}", this.getClass().getName());
            HttpRequest httpRequest = (HttpRequest) httpObject;
            List<String> headerValues = GatewayConstant.getHeaderValues(originalRequest, "Cookie");
            if (headerValues.size() > 0 && headerValues.get(0) != null) {
                String[] cookies = headerValues.get(0).split(";");
                for (String cookie : cookies) {
                    for (Pattern pat : ConfUtil.getPattern(FilterType.COOKIE.name())) {
                        Matcher matcher = pat.matcher(cookie.toLowerCase());
                        if (matcher.find()) {
                            hackLog(logger, GatewayConstant.getRealIp(httpRequest, channelHandlerContext), FilterType.COOKIE.name(), pat.toString());
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
