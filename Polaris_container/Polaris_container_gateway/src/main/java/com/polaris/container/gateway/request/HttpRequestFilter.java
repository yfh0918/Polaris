package com.polaris.container.gateway.request;

import org.slf4j.Logger;

import com.polaris.container.gateway.HttpFilter;
import com.polaris.core.component.LifeCycle;

import io.netty.handler.codec.http.HttpRequest;

/**
 * @author:Tom.Yu
 * <p>
 * Description:
 * <p>
 * HTTP Request拦截器抽象类
 */
public abstract class HttpRequestFilter extends HttpFilter<HttpRequest>  {
	
    /**
     * 构造函数并加入调用链
     *
     */
	@Override
	public void starting(LifeCycle event) {
		super.starting(event);
		HttpRequestFilterChain.INSTANCE.add(this);
	} 
	
    /**
     * 从调用链去除过滤器
     *
     */
	@Override
	public void stopping(LifeCycle event) {
		super.stopping(event);
		HttpRequestFilterChain.INSTANCE.remove(this);
	}
	
    /**
     * 是否是黑名单
     *
     * @return 黑名单返回true, 白名单返回false, 白名单的实现类要重写次方法
     */
    public boolean isBlacklist() {
        return true;
    }
    
    /**
     * 记录hack日志
     *
     * @param realIp 用户IP
     * @param logger 日志logger
     * @param type   匹配的类型
     * @param cause  被拦截的原因
     */
    public void hackLog(Logger logger, String realIp, String type, String cause) {
        if (isBlacklist()) {
            logger.info("type:{},realIp:{},cause:{}", type, realIp, cause);
        } else {
            logger.debug("type:{},realIp:{},cause:{}", type, realIp, cause);
        }
    }
}
