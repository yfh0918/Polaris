package com.polaris.container.gateway.response;

import java.util.Collections;

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

    public static void doFilter(HttpRequest originalRequest, HttpResponse httpResponse) {
        for (HttpResponseFilter filter : responseFilters) {
        	String key = HttpFilterEnum.getKey(filter.getClass());
        	if (StringUtil.isEmpty(key)) {
        		continue;
        	}
        	if (GatewayConstant.OFF.equals(ConfClient.get(key))) {
        		continue;
        	}
            filter.doFilter(originalRequest, httpResponse);
        }
    }
    
    
}
