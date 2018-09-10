package com.polaris.gateway.response;

import java.util.Collections;

import com.polaris.comm.config.ConfClient;
import com.polaris.gateway.GatewayConstant;
import com.polaris.gateway.HttpFilterChain;
import com.polaris.gateway.HttpFilterCompare;
import com.polaris.gateway.HttpFilterEnum;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

/**
 * @author:Winning
 *
 * Description:
 *
 */
public class HttpResponseFilterChain extends HttpFilterChain {

    public synchronized static void addFilter(HttpResponseFilter filter) {
		if (!GatewayConstant.OFF.equals(ConfClient.get(HttpFilterEnum.getSwitch(filter.getClass())))) {
			responseFilters.add(filter);
	        Collections.sort(responseFilters, new HttpFilterCompare());
		}
    }

    public static void doFilter(HttpRequest originalRequest, HttpResponse httpResponse) {
        for (HttpResponseFilter filter : responseFilters) {
            filter.doFilter(originalRequest, httpResponse);
        }
    }
    
    
}
