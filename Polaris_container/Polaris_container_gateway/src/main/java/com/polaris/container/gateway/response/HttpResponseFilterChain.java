package com.polaris.container.gateway.response;

import java.util.Collections;

import org.apache.commons.lang3.tuple.ImmutablePair;

import com.polaris.container.gateway.GatewayConstant;
import com.polaris.container.gateway.HttpFilterChain;
import com.polaris.container.gateway.HttpFilterCompare;
import com.polaris.container.gateway.HttpFilterEnum;
import com.polaris.core.config.ConfClient;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import jodd.util.StringUtil;

/**
 * @author:Tom.Yu
 *
 * Description:
 *
 */
public class HttpResponseFilterChain extends HttpFilterChain {

    public synchronized static void addFilter(HttpResponseFilter filter) {
		responseFilters.add(filter);
        Collections.sort(responseFilters, new HttpFilterCompare());
    }

    public static ImmutablePair<Boolean, HttpResponseFilter> doFilter(HttpRequest originalRequest, HttpResponse httpResponse) {
        for (HttpResponseFilter filter : responseFilters) {
        	String key = HttpFilterEnum.getKey(filter.getClass());
        	if (StringUtil.isEmpty(key)) {
        		continue;
        	}
        	if (GatewayConstant.OFF.equals(ConfClient.get(key))) {
        		continue;
        	}
            boolean result = filter.doFilter(originalRequest, httpResponse);
            if (result) {
            	return new ImmutablePair<>(true, filter);
            }
        }
        return new ImmutablePair<>(false, null);
    }
    
    
}
