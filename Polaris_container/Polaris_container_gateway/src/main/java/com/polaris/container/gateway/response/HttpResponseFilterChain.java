package com.polaris.container.gateway.response;

import java.util.Collections;

import org.apache.commons.lang3.tuple.ImmutablePair;

import com.polaris.container.gateway.GatewayConstant;
import com.polaris.container.gateway.HttpFilterChain;
import com.polaris.container.gateway.HttpFilterCompare;
import com.polaris.container.gateway.pojo.HttpFilterEntity;
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

    public synchronized static void addFilter(HttpFilterEntity httpFilterEntity) {
		responseFilters.add((HttpResponseFilter)httpFilterEntity.getFilter());
        Collections.sort(responseFilters, new HttpFilterCompare());
    }
    public synchronized static void removeFilter(HttpFilterEntity httpFilterEntity) {
    	responseFilters.remove(httpFilterEntity.getFilter());
    }
    public static ImmutablePair<Boolean, HttpResponseFilter> doFilter(HttpRequest originalRequest, HttpResponse httpResponse) {
        for (HttpResponseFilter filter : responseFilters) {
        	HttpFilterEntity httpFilterEntity = filter.getHttpFilterEntity();
        	if (httpFilterEntity == null) {
        		continue;
        	}
        	String key = httpFilterEntity.getKey();
        	if (StringUtil.isEmpty(key)) {
        		continue;
        	}
        	if (!GatewayConstant.ON.equals(ConfClient.get(key))) {
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
