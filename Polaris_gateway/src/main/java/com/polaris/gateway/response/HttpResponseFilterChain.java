package com.polaris.gateway.response;

import java.util.Collections;

import com.polaris.core.config.ConfClient;
import com.polaris.gateway.GatewayConstant;
import com.polaris.gateway.HttpFilterChain;
import com.polaris.gateway.HttpFilterCompare;
import com.polaris.gateway.HttpFilterEnum;

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
        	String strSwitch = HttpFilterEnum.getSwitch(filter.getClass());
        	if (StringUtil.isEmpty(strSwitch)) {
        		continue;
        	}
        	if (GatewayConstant.OFF.equals(ConfClient.get(strSwitch))) {
        		continue;
        	}
            filter.doFilter(originalRequest, httpResponse);
        }
    }
    
    
}
