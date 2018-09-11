package com.polaris.gateway.request;

import java.util.Collections;

import org.apache.commons.lang3.tuple.ImmutablePair;

import com.polaris.comm.config.ConfClient;
import com.polaris.gateway.GatewayConstant;
import com.polaris.gateway.HttpFilterChain;
import com.polaris.gateway.HttpFilterCompare;
import com.polaris.gateway.HttpFilterEnum;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;

/**
 * @author:Tom.Yu
 * <p>
 * Description:
 * <p>
 * 拦截器链
 */
public class HttpRequestFilterChain extends HttpFilterChain{

    public synchronized static void addFilter(HttpRequestFilter filter) {
		if (!GatewayConstant.OFF.equals(ConfClient.get(HttpFilterEnum.getSwitch(filter.getClass())))) {
	        requestFilters.add(filter);
	        Collections.sort(requestFilters, new HttpFilterCompare());
		}
    }

    public static ImmutablePair<Boolean, HttpRequestFilter> doFilter(HttpRequest originalRequest, HttpObject httpObject, ChannelHandlerContext channelHandlerContext) {
        for (HttpRequestFilter filter : requestFilters) {
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
