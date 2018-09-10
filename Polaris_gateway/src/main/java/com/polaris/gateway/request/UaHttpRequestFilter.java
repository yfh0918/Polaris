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
 * @author:Winning
 *
 * Description:
 *
 */
@Service
public class UaHttpRequestFilter extends HttpRequestFilter {
	private static LogUtil logger = LogUtil.getInstance(UaHttpRequestFilter.class);

    @Override
    public boolean doFilter(HttpRequest originalRequest, HttpObject httpObject, ChannelHandlerContext channelHandlerContext) {
        if (httpObject instanceof HttpRequest) {
            logger.debug("filter:{}", this.getClass().getName());
            HttpRequest httpRequest = (HttpRequest) httpObject;
            List<String> headerValues = GatewayConstant.getHeaderValues(originalRequest, "User-Agent");
            if (headerValues.size() > 0 && headerValues.get(0) != null) {
                for (Pattern pat : ConfUtil.getPattern(FilterType.UA.name())) {
                    Matcher matcher = pat.matcher(headerValues.get(0));
                    if (matcher.find()) {
                        hackLog(logger, GatewayConstant.getRealIp(httpRequest, channelHandlerContext), FilterType.UA.name(), pat.toString());
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
