package com.polaris.container.gateway.request;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.container.gateway.HttpFilterConstant;
import com.polaris.container.gateway.pojo.HttpFilterFile;

import io.netty.channel.ChannelHandlerContext;
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
	private Set<String> patterns = new HashSet<>();

	@Override
	public void onChange(HttpFilterFile file) {
		Set<String> data = file.getData();
		if (data != null) {
			patterns = data;
		}
	}
	
    @Override
    public boolean doFilter(HttpRequest originalRequest, HttpObject httpObject, ChannelHandlerContext channelHandlerContext) {
        if (httpObject instanceof HttpRequest) {
            logger.debug("filter:{}", this.getClass().getName());
            HttpRequest httpRequest = (HttpRequest) httpObject;
            String realIp = HttpFilterConstant.getRealIp(httpRequest);
            if (patterns.contains(realIp)) {
                hackLog(logger, HttpFilterConstant.getRealIp(httpRequest), HttpIpRequestFilter.class.getSimpleName(), "black ip");
                return true;
            }
        }
        return false;
    }
}
