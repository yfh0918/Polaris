package com.polaris.demo.gateway.request;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.container.gateway.HttpConstant;
import com.polaris.container.gateway.pojo.HttpFile;
import com.polaris.container.gateway.pojo.HttpFilterMessage;
import com.polaris.container.gateway.request.HttpRequestFilter;
import com.polaris.core.pojo.KeyValuePair;
import com.polaris.core.thread.ThreadPoolBuilder;
import com.polaris.core.util.HttpClientUtil;
import com.polaris.core.util.PropertyUtil;
import com.polaris.core.util.StringUtil;
import com.polaris.core.util.SystemCallUtil;

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
 * Statistics
 */
public class StatisticsRequestFilter extends HttpRequestFilter {
	private static Logger logger = LoggerFactory.getLogger(StatisticsRequestFilter.class);
	private ThreadPoolExecutor threadPool = null;
    private CloseableHttpClient httpClient = null;
	private static volatile Set<String> uriSet = new HashSet<>();
	private static String connectUrl = null;
    
	@Override
	public void onChange(HttpFile file) {
	    Set<String> tempUriSet = new HashSet<>();
	    String tempConnectUrl = null;
        for (String conf : file.getData()) {
            KeyValuePair kv = PropertyUtil.getKVPair(conf);
            if (kv != null) {
                if (kv.getKey().equals("STATISTICS_PATHS")) {
                    try {
                        String[] uris = kv.getValue().split(",");
                        for (String uri : uris) {
                            tempUriSet.add(uri);
                        }
                    } catch (Exception ex) {
                    }
                }
                if (kv.getKey().equals("CONNECT_URL")) {
                    try {
                        if (StringUtil.isNotEmpty(kv.getValue())) {
                            tempConnectUrl = kv.getValue();
                        }
                    } catch (Exception ex) {
                    }
                }
            }
        }
        uriSet = tempUriSet;
        connectUrl = tempConnectUrl;
    }
	
	@Override
	public void doStart() {
        threadPool = ThreadPoolBuilder.newBuilder()
                                      .poolName("BanyanStatisticsRequestFilter Thread Pool")
                                      .coreThreads(1)
                                      .maximumThreads(1)
                                      .keepAliveSeconds(10l)
                                      .workQueue(new LinkedBlockingDeque<Runnable>(10000))
                                      .build();
        httpClient = HttpClientUtil.createHttpClient(5, 1, 0);
    }
	@Override
	public void doStop() {
		try {
		    ThreadPoolBuilder.destroy(threadPool);
            httpClient.close();
        } catch (IOException e) {
            //ignore
        }
	}
    
	@Override
    public boolean doFilter(HttpRequest originalRequest, HttpObject httpObject, HttpFilterMessage httpMessage) {
        if (httpObject instanceof HttpRequest) {
            logger.debug("filter:{}", this.getClass().getName());
            String realIp = HttpConstant.getRealIp((DefaultHttpRequest) httpObject);
            
            //控制总流量，超标直接返回
            HttpRequest httpRequest = (HttpRequest)httpObject;
            
            //获取url
            String orgUri = httpRequest.uri();
            String uri;
            String queryParameter;
            int index = orgUri.indexOf("?");
            if (index > 0) {
                uri = orgUri.substring(0, index);
                queryParameter = orgUri.substring(index + 1);
            } else {
                uri = orgUri;
                queryParameter = "";
            }
            
            //统计
            saveStatistics(uri,queryParameter, realIp);
            
        }
        return false;
    }
	
    public void saveStatistics(String uri, String queryParameter, String realIp) {
        try {
            if (!uriSet.contains(uri) || StringUtil.isEmpty(connectUrl)) {
                return;
            }
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    Map<String, Object> requestParams = new HashMap<>();
                    requestParams.put("ip", realIp);
                    requestParams.put("uri", uri);
                    requestParams.put("queryParameter", queryParameter);
                    Map<String, String> headerParams = new HashMap<>();
                    headerParams.put(SystemCallUtil.key(), SystemCallUtil.value());
                    try {
                        HttpClientUtil.post(connectUrl, 1000, requestParams, headerParams, httpClient);
                    } catch (Exception ex) {
                        logger.error("Error:",ex);
                    }
                }
            });
        } catch (Exception ex) {
            logger.error("Error:",ex);
        }
    }
    
    
}


