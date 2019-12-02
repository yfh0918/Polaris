package com.polaris.gateway.request;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.adapter.servlet.callback.UrlCleaner;
import com.alibaba.csp.sentinel.adapter.servlet.callback.WebCallbackManager;
import com.alibaba.csp.sentinel.context.ContextUtil;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.RateLimiter;
import com.polaris.core.Constant;
import com.polaris.core.config.ConfClient;
import com.polaris.gateway.GatewayConstant;
import com.polaris.gateway.support.HttpRequestFilterSupport;

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
	private static Logger logger = LoggerFactory.getLogger(CCHttpRequestFilter.class);
	
	//控制每个IP地址的访问率
	private volatile LoadingCache<String, AtomicInteger> loadingCache;
    
    //控制总的流量
	private volatile RateLimiter totalRateLimiter;
	private volatile String ip_rate;
	private volatile int int_ip_rate = 0;
	private volatile String flow_control_rate;

	public CCHttpRequestFilter() {

    	//创建总的访问令牌
		flow_control_rate = ConfClient.get("gateway.flowcontrol.rate");
		totalRateLimiter = RateLimiter.create(Integer.parseInt(flow_control_rate));
    			
    	//IP单位的缓存
		ip_rate = ConfClient.get("gateway.cc.rate");
		int_ip_rate = Integer.parseInt(ip_rate);
        loadingCache = CacheBuilder.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(1, TimeUnit.SECONDS)
                .build(new CacheLoader<String, AtomicInteger>() {
                    @Override
                    public AtomicInteger load(String key) throws Exception {
                    	return new AtomicInteger(0);
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
            HttpRequest httpRequest = (HttpRequest)httpObject;
            if (!totalRateLimiter.tryAcquire()) {
            	String message = "flows access per second has exceeded " + ConfClient.get("gateway.flowcontrol.rate");
            	this.setResultDto(HttpRequestFilterSupport.createResultDto(Constant.RESULT_FAIL,message));
                hackLog(logger, GatewayConstant.getRealIp((DefaultHttpRequest) httpObject), "cc", message);
                return true;
            }
            
            //对各个URL资源进行熔断拦截
            if (doSentinel(httpRequest)) {
            	String message = httpRequest.uri() + " access  per second has exceeded ";
            	this.setResultDto(HttpRequestFilterSupport.createResultDto(Constant.RESULT_FAIL,message));
                hackLog(logger, GatewayConstant.getRealIp((DefaultHttpRequest) httpObject), "cc", message);
            	return true;
            }
            
            //单个IP最大访问速率gateway.cc.rate
            if (!ConfClient.get("gateway.cc.rate").equals(ip_rate)) {
            	synchronized(this) {
            		if (!ConfClient.get("gateway.cc.rate").equals(ip_rate)) {
            			ip_rate = ConfClient.get("gateway.cc.rate");
            			int_ip_rate = Integer.parseInt(ip_rate);
            			loadingCache = CacheBuilder.newBuilder()
            	                .maximumSize(1000)
            	                .expireAfterWrite(1, TimeUnit.SECONDS)
            	                .build(new CacheLoader<String, AtomicInteger>() {
            	                    @Override
            	                    public AtomicInteger load(String key) throws Exception {
            	                    	return new AtomicInteger(0);
            	                    }
            	                });
            		}
            	}
            }
            AtomicInteger rateLimiter = null;
            try {
                rateLimiter = (AtomicInteger) loadingCache.get(realIp);
                int count = rateLimiter.incrementAndGet();
                if (count > int_ip_rate) {
                	String message = realIp + " access " +count+ " per second has exceeded " + ConfClient.get("gateway.cc.rate");
                	this.setResultDto(HttpRequestFilterSupport.createResultDto(Constant.RESULT_FAIL,message));
                	hackLog(logger, GatewayConstant.getRealIp((DefaultHttpRequest) httpObject), "cc", message);
                	return true;
                }
            } catch (ExecutionException e) {
            	logger.error(e.getMessage());
            	return true;
            }
        }
        return false;
    }
	
	//目标拦截
    private boolean doSentinel(HttpRequest httpRequest) {
    	Entry entry = null;
    	try {
            UrlCleaner urlCleaner = WebCallbackManager.getUrlCleaner();
            //获取url
            String uri = httpRequest.uri();
            String url;
            int index = uri.indexOf("?");
            if (index > 0) {
                url = uri.substring(0, index);
            } else {
                url = uri;
            }
            
            if (urlCleaner != null) {
            	url = urlCleaner.clean(url);
            }
            ContextUtil.enter(url);
            SphU.entry(url, EntryType.IN);
            return false;
        } catch (BlockException e) {
        	return true;
        } finally {
        	if (entry != null) {
                entry.exit();
            }
            ContextUtil.exit();
        }
    }
}


