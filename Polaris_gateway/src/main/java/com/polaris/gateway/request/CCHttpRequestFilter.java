package com.polaris.gateway.request;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.RateLimiter;
import com.polaris.comm.config.ConfClient;
import com.polaris.comm.util.LogUtil;
import com.polaris.gateway.GatewayConstant;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;

/**
 * @author:Tom.Yu
 *
 * Description:
 * cc拦截
 */
/**
 * @author:Tom.Yu
 *
 * Description:
 * cc拦截
 */
@Service
public class CCHttpRequestFilter extends HttpRequestFilter {
	private static LogUtil logger = LogUtil.getInstance(CCHttpRequestFilter.class);
	
	//控制每个IP地址的访问率
	private volatile LoadingCache<String, RateLimiter> loadingCache;
	private volatile Map<String, AtomicInteger> countMap = new HashMap<>();
	private static final int IP_MAX_WAIT_COUNT = 5;//最对同一个IP有5个线程等待处理
    
    //控制总的流量
	private volatile RateLimiter totalRateLimiter;
	private volatile String ip_rate;
	private volatile String flow_control_rate;

	public CCHttpRequestFilter() {

    	//创建总的访问令牌
		flow_control_rate = ConfClient.get("gateway.flowcontrol.rate");
		totalRateLimiter = RateLimiter.create(Integer.parseInt(flow_control_rate));
    			
    	//IP单位的缓存
		ip_rate = ConfClient.get("gateway.cc.rate");
        loadingCache = CacheBuilder.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(1, TimeUnit.SECONDS)
                .build(new CacheLoader<String, RateLimiter>() {
                    @Override
                    public RateLimiter load(String key) throws Exception {
                        RateLimiter rateLimiter = RateLimiter.create(Integer.parseInt(ip_rate));
                        countMap.put(key, new AtomicInteger(0));
                        logger.debug("RateLimiter for key:{} have been created", key);
                        return rateLimiter;
                    }
                });

    }

	@Override
    public boolean doFilter(HttpRequest originalRequest, HttpObject httpObject, ChannelHandlerContext channelHandlerContext) {
        if (httpObject instanceof HttpRequest) {
            logger.debug("filter:{}", this.getClass().getName());
            String realIp = GatewayConstant.getRealIp((DefaultHttpRequest) httpObject);
            
        	//总流量控制发生变化
            if (!ConfClient.get("gateway.flowcontrol.rate").equals(flow_control_rate)) {
            	synchronized(this) {
            		if (!ConfClient.get("gateway.flowcontrol.rate").equals(flow_control_rate)) {
            			flow_control_rate = ConfClient.get("gateway.flowcontrol.rate");
            			totalRateLimiter = RateLimiter.create(Integer.parseInt(flow_control_rate));
            		}
            	}
            }
            
            //控制总流量，超标直接返回
            if (!totalRateLimiter.tryAcquire()) {
                hackLog(logger, GatewayConstant.getRealIp((DefaultHttpRequest) httpObject), "cc", ConfClient.get("gateway.cc.rate"));
                return true;
            }
            
            //单个IP最大访问速率gateway.cc.rate
            if (!ConfClient.get("gateway.cc.rate").equals(ip_rate)) {
            	synchronized(this) {
            		if (!ConfClient.get("gateway.cc.rate").equals(ip_rate)) {
            			ip_rate = ConfClient.get("gateway.cc.rate");
            			loadingCache = CacheBuilder.newBuilder()
            	                .maximumSize(1000)
            	                .expireAfterWrite(1, TimeUnit.SECONDS)
            	                .build(new CacheLoader<String, RateLimiter>() {
            	                    @Override
            	                    public RateLimiter load(String key) throws Exception {
            	                        RateLimiter rateLimiter = RateLimiter.create(Integer.parseInt(ip_rate));
            	                        countMap.put(key, new AtomicInteger(0));
            	                        logger.debug("RateLimiter for key:{} have been created", key);
            	                        return rateLimiter;
            	                    }
            	                });
            		}
            	}
            }
            RateLimiter rateLimiter = null;
            try {
                rateLimiter = (RateLimiter) loadingCache.get(realIp);
                int count = countMap.get(realIp).incrementAndGet();
                int orgCount = Integer.parseInt(ConfClient.get("gateway.cc.maxcount", String.valueOf(IP_MAX_WAIT_COUNT)));
                if (count > orgCount) {
                	hackLog(logger, GatewayConstant.getRealIp((DefaultHttpRequest) httpObject), "cc", ConfClient.get("gateway.cc.rate"));
                	return true;
                }
            } catch (ExecutionException e) {
            	logger.error(e);
            	return true;
            }
            rateLimiter.acquire();
            countMap.get(realIp).decrementAndGet();
        }
        return false;
    }
}


