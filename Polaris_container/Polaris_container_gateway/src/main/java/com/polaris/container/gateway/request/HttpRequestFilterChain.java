package com.polaris.container.gateway.request;

import org.apache.commons.lang3.tuple.ImmutablePair;

import com.polaris.container.gateway.HttpFilterChain;
import com.polaris.container.gateway.HttpFilterConstant;
import com.polaris.container.gateway.pojo.HttpFilterEntity;
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
public class HttpRequestFilterChain extends HttpFilterChain<HttpRequestFilter>{
    
    public static HttpRequestFilterChain INSTANCE = new HttpRequestFilterChain();
    private HttpRequestFilterChain() {}


    public ImmutablePair<Boolean, HttpRequestFilter> doFilter(HttpRequest originalRequest, HttpObject httpObject, ChannelHandlerContext channelHandlerContext) {
        for (HttpRequestFilter filter : filters) {
        	//判断
        	HttpFilterEntity httpFilterEntity = filter.getHttpFilterEntity();
        	if (httpFilterEntity == null) {
        		continue;
        	}
        	String key = httpFilterEntity.getKey();
        	if (StringUtil.isEmpty(key)) {
        		continue;
        	}
        	if (!HttpFilterConstant.ON.equals(ConfClient.get(key))) {
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
