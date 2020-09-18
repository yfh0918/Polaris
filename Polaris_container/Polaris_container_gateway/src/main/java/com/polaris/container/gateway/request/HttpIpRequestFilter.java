package com.polaris.container.gateway.request;

import java.util.LinkedHashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.container.gateway.HttpConstant;
import com.polaris.container.gateway.pojo.HttpFile;
import com.polaris.container.gateway.pojo.HttpFilterMessage;
import com.polaris.container.gateway.util.FileReaderUtil;

import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;

/**
 * @author:Tom.Yu
 *
 * Description:
 *
 * IP黑名单拦截
 */
public class HttpIpRequestFilter extends HttpRequestFilter {
	private static Logger logger = LoggerFactory.getLogger(HttpIpRequestFilter.class);
	private Set<String> ipSet = new LinkedHashSet<>();

	@Override
	public void onChange(HttpFile file) {
		ipSet = FileReaderUtil.getDataSet(file.getData());
	}
	
    @Override
    public boolean doFilter(HttpRequest originalRequest,HttpObject httpObject, HttpFilterMessage httpMessage) {
        if (httpObject instanceof HttpRequest) {
            logger.debug("filter:{}", this.getClass().getName());
            HttpRequest httpRequest = (HttpRequest) httpObject;
            String realIp = HttpConstant.getRealIp(httpRequest);
            if (ipSet.contains(realIp)) {
                hackLog(logger, HttpConstant.getRealIp(httpRequest), HttpIpRequestFilter.class.getSimpleName(), "black ip");
                return true;
            }
        }
        return false;
    }
}
