package com.polaris.container.gateway.response;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

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
public class HttpResponseFilterChain implements HttpFilterChain<HttpResponseFilter> {
    protected List<HttpResponseFilter> responseFilters = new CopyOnWriteArrayList<>();
    
    public static HttpResponseFilterChain INSTANCE = new HttpResponseFilterChain();
    private HttpResponseFilterChain() {}

    @Override
    public void add(HttpResponseFilter filter) {
		responseFilters.add(filter);
        Collections.sort(responseFilters, new HttpFilterCompare());
    }
    
    @Override
    public void remove(HttpResponseFilter filter) {
    	responseFilters.remove(filter);
    }
    
    public ImmutablePair<Boolean, HttpResponseFilter> doFilter(HttpRequest originalRequest, HttpResponse httpResponse) {
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
