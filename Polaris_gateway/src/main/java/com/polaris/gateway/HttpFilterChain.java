package com.polaris.gateway;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.polaris.gateway.request.HttpRequestFilter;
import com.polaris.gateway.response.HttpResponseFilter;
import com.polaris.comm.config.ConfClient;
import com.polaris.comm.util.SpringUtil;

public abstract class HttpFilterChain {
    protected static List<HttpRequestFilter> requestFilters = new CopyOnWriteArrayList<>();
    protected static List<HttpResponseFilter> responseFilters = new CopyOnWriteArrayList<>();

    //监听过滤器是否发生变化
    public static void watchFilters() {
    	for (HttpFilterEnum e : HttpFilterEnum.values()) { 
    		
    		//需要过滤
    		if (!GatewayConstant.OFF.equals(ConfClient.get(e.getSwitch()))) {
    			
    			//request
    			if (SpringUtil.getBean(e.getClazz()) instanceof HttpRequestFilter) {
    				boolean isFind = false;
    				for (HttpRequestFilter request : requestFilters) {
        				if (request.getClass() == e.getClazz()) {
        					isFind = true;
        					break;
        				}
        			}
    				//没有发现
    				if (!isFind) {
    					requestFilters.add((HttpRequestFilter)SpringUtil.getBean(e.getClazz()));
    		    		Collections.sort(requestFilters, new HttpFilterCompare());
    				}
    			}
    			
    			//response
    			if (SpringUtil.getBean(e.getClazz()) instanceof HttpResponseFilter) {
    				boolean isFind = false;
    				for (HttpResponseFilter response : responseFilters) {
        				if (response.getClass() == e.getClazz()) {
        					isFind = true;
        					break;
        				}
        			}
    				//没有发现
    				if (!isFind) {
    					responseFilters.add((HttpResponseFilter)SpringUtil.getBean(e.getClazz()));
    					Collections.sort(responseFilters, new HttpFilterCompare());
    				}
    			}
    			
        	//不需要过滤
    		} else {
    			
    			//request
    			if (SpringUtil.getBean(e.getClazz()) instanceof HttpRequestFilter) {
    				for (int index = 0; index < requestFilters.size(); index++) {
        				if (requestFilters.get(index).getClass() == e.getClazz()) {
        					requestFilters.remove(index);
        					break;
        				}
        			}
    			}
    			
    			//response
    			if (SpringUtil.getBean(e.getClazz()) instanceof HttpResponseFilter) {
    				for (int index = 0; index < responseFilters.size(); index++) {
        				if (responseFilters.get(index).getClass() == e.getClazz()) {
        					responseFilters.remove(index);
        					break;
        				}
        			}
    			}
    		}
    	}
    }
}
