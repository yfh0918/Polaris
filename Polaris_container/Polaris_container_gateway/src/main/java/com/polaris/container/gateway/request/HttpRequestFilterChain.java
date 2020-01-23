package com.polaris.container.gateway.request;

import java.util.Collections;

import org.apache.commons.lang3.tuple.ImmutablePair;

import com.polaris.container.gateway.GatewayConstant;
import com.polaris.container.gateway.HttpFilterChain;
import com.polaris.container.gateway.HttpFilterCompare;
import com.polaris.container.gateway.HttpFilterEnum;
import com.polaris.core.config.ConfClient;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import jodd.util.StringUtil;

/**
 * @author:Tom.Yu
 * <p>
 * Description:
 * <p>
 * 拦截器链
 */
public class HttpRequestFilterChain extends HttpFilterChain{

    public synchronized static void addFilter(HttpRequestFilter filter) {
        requestFilters.add(filter);
        Collections.sort(requestFilters, new HttpFilterCompare());
    }

    public static ImmutablePair<Boolean, HttpRequestFilter> doFilter(HttpRequest originalRequest, HttpObject httpObject, ChannelHandlerContext channelHandlerContext) {
        for (HttpRequestFilter filter : requestFilters) {
        	//判断
        	String key = HttpFilterEnum.getKey(filter.getClass());
        	if (StringUtil.isEmpty(key)) {
        		continue;
        	}
        	if (GatewayConstant.OFF.equals(ConfClient.get(key))) {
        		continue;
        	}
            boolean result = filter.doFilter(originalRequest, httpObject, channelHandlerContext);
            if (result && filter.isBlacklist()) {
                return new ImmutablePair<>(true, filter);
            } else if (result && !filter.isBlacklist()) {
                break;
            }
        }
        return new ImmutablePair<>(false, null);
    }

}
