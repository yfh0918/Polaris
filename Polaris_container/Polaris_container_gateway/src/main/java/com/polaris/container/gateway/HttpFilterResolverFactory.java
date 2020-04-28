package com.polaris.container.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.container.gateway.proxy.DefaultHostResolver;
import com.polaris.container.gateway.proxy.DnsSecServerResolver;
import com.polaris.container.gateway.proxy.HostResolver;
import com.polaris.core.config.ConfClient;

public abstract class HttpFilterResolverFactory {
	private static Logger logger = LoggerFactory.getLogger(HttpFilterResolverFactory.class);
	public static HostResolver get() {
        String proxy_type = ConfClient.get("server.proxy.type",HttpFilterConstant.REVERSE_PROXY);
        if (HttpFilterConstant.CONNECT_PROXY.equals(proxy_type)) {
            logger.info("Connect模式开启");
        	return new DefaultHostResolver();
        } else if (HttpFilterConstant.CONNECT_PROXY.equals(proxy_type)) {
            logger.info("DNS模式开启");
        	return new DnsSecServerResolver();
        } else {
            logger.info("反向代理模式开启");
            return HttpFilterHostResolver.INSTANCE;
        }
	}
}
