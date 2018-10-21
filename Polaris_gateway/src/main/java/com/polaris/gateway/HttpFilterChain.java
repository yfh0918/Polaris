package com.polaris.gateway;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.polaris.gateway.request.HttpRequestFilter;
import com.polaris.gateway.response.HttpResponseFilter;

public abstract class HttpFilterChain {
    protected static List<HttpRequestFilter> requestFilters = new CopyOnWriteArrayList<>();
    protected static List<HttpResponseFilter> responseFilters = new CopyOnWriteArrayList<>();
}
