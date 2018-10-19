package com.polaris.gateway.request;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.common.util.concurrent.RateLimiter;
import com.polaris.gateway.GatewayConstant;
import com.polaris.gateway.support.HttpRequestFilterSupport;
import com.polaris.comm.Constant;
import com.polaris.comm.config.ConfClient;
import com.polaris.comm.util.LogUtil;

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
@Service
public class CCHttpRequestFilter extends HttpRequestFilter {
	private static LogUtil logger = LogUtil.getInstance(CCHttpRequestFilter.class);
	
	//控制每个IP地址的访问率
	private volatile LoadingCache<String, RateLimiter> loadingCache;
    
    //控制总的流量
	private volatile RateLimiter totalRateLimiter;
	private volatile String flow_control_rate;

	public CCHttpRequestFilter() {

    	//创建总的访问令牌
		flow_control_rate = ConfClient.get("gateway.flowcontrol.rate");
		totalRateLimiter = RateLimiter.create(Integer.parseInt(flow_control_rate));
    			
    	//IP单位的缓存
        loadingCache = CacheBuilder.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(2, TimeUnit.SECONDS)
                .removalListener(new RemovalListener<String, RateLimiter>() {
                    @Override
                    public void onRemoval(RemovalNotification<String, RateLimiter> notification) {
                        logger.debug("key:{} remove from cache", notification.getKey());
                    }
                })
                .build(new CacheLoader<String, RateLimiter>() {
                    @Override
                    public RateLimiter load(String key) throws Exception {
                        RateLimiter rateLimiter = RateLimiter.create(Integer.parseInt(ConfClient.get("gateway.cc.rate")));
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
            RateLimiter rateLimiter = null;
            try {
                rateLimiter = (RateLimiter) loadingCache.get(realIp);
            } catch (ExecutionException e) {
            	logger.error(e);
            }
            
            //最大访问速率gateway.cc.rate
            if (rateLimiter.tryAcquire()) {
            	
            	//总流量控制发生变化
                if (!ConfClient.get("gateway.flowcontrol.rate").equals(flow_control_rate)) {
                	synchronized(this) {
                		if (!ConfClient.get("gateway.flowcontrol.rate").equals(flow_control_rate)) {
                			flow_control_rate = ConfClient.get("gateway.flowcontrol.rate");
                			totalRateLimiter = RateLimiter.create(Integer.parseInt(flow_control_rate));
                		}
                	}
                }
                
                //最大访问速率gateway.cc.rate
                if (totalRateLimiter.tryAcquire()) {
                    return false;
                } else {
                	this.setResultDto(HttpRequestFilterSupport.createResultDto(Constant.MESSAGE_GLOBAL_ERROR));
                    hackLog(logger, GatewayConstant.getRealIp((DefaultHttpRequest) httpObject), "cc", ConfClient.get("gateway.cc.rate"));
                    return true;
                }
            } else {
            	this.setResultDto(HttpRequestFilterSupport.createResultDto(Constant.MESSAGE_GLOBAL_ERROR));
                hackLog(logger, GatewayConstant.getRealIp((DefaultHttpRequest) httpObject), "cc", ConfClient.get("gateway.cc.rate"));
                return true;
            }
        }
        return false;
    }
}
