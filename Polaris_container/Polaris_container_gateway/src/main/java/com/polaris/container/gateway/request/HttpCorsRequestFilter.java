package com.polaris.container.gateway.request;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.container.gateway.pojo.HttpFilterFile;
import com.polaris.core.util.PropertyUtil;
import com.polaris.core.util.StringUtil;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;

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
 * 跨域请求
 */
public class HttpCorsRequestFilter extends HttpRequestFilter {
	private static Logger logger = LoggerFactory.getLogger(HttpCorsRequestFilter.class);
	private static Map<String, String> corsMap = new HashMap<>(); 
	private final String DEFAULT_COSR_BODY = "OPTIONS,HEAD,GET,POST";
	private final String CORS_BODY_KEY="Access-Control-Response";

	
	@Override
	public void onChange(HttpFilterFile file) {
    	Map<String, String> tempCorsMap = new HashMap<>(); 
    	for (String conf : file.getData()) {
			if (!conf.startsWith("#")) {
				String[] kv = PropertyUtil.getKeyValue(conf);
				if (kv != null && kv.length == 2) {
					if (StringUtil.isNotEmpty(kv[1])) {
						tempCorsMap.put(kv[0], kv[1]);
					}
				}
			}
    	}
    	corsMap = tempCorsMap;
    }
    
    public static Map<String, String> getCorsMap() {
    	return corsMap;
    }
    
	@Override
    public boolean doFilter(HttpRequest originalRequest, HttpObject httpObject, ChannelHandlerContext channelHandlerContext) {
        if (httpObject instanceof HttpRequest) {
            logger.debug("filter:{}", this.getClass().getName());
            
            //获取request
            HttpRequest httpRequest = (HttpRequest)httpObject;

            //判断是否为OPTION
            if (httpRequest.method() == HttpMethod.OPTIONS) {
            	if (StringUtil.isNotEmpty(corsMap.get(CORS_BODY_KEY))) {
            		this.setResult(corsMap.get(CORS_BODY_KEY));
            	} else {
                	this.setResult(DEFAULT_COSR_BODY);
            	}
            	this.setStatus(HttpResponseStatus.OK);
            	return true;
            }
            
        }
        return false;
    }
	
	

}


