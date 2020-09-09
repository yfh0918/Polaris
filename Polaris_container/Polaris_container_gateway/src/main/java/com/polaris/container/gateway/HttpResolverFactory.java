package com.polaris.container.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.container.gateway.proxy.HostResolver;

public abstract class HttpResolverFactory {
	private static Logger logger = LoggerFactory.getLogger(HttpResolverFactory.class);
	public static HostResolver get() {
        logger.info("反向代理模式开启");
        return HttpHostContextResolver.INSTANCE;
	}
}
