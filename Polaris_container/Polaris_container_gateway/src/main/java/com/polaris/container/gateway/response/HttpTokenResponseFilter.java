package com.polaris.container.gateway.response;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.polaris.container.gateway.request.HttpTokenRequestFilter;
import com.polaris.core.util.JwtUtil;
import com.polaris.core.util.ResultUtil;
import com.polaris.core.util.StringUtil;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * @author:Tom.Yu
 *
 * Description:
 *
 */
public class HttpTokenResponseFilter extends HttpResponseFilter {
	private static Logger logger = LoggerFactory.getLogger(HttpTokenResponseFilter.class);
    @Override
    public boolean doFilter(HttpRequest originalRequest, HttpResponse httpResponse) {
    	try {
        	String url = HttpTokenRequestFilter.getUrl(originalRequest);
        	if (HttpTokenRequestFilter.isTokenPath(url)) {
        		String jwtInfo = httpResponse.headers().get(JwtUtil.JWT_KEY);
         		if (StringUtil.isNotEmpty(jwtInfo)) {
        			Map<String, Object> requestDto =  JSONObject.parseObject(JwtUtil.decode(jwtInfo));
        			if (requestDto != null) {
        				String token = JwtUtil.createJWT(requestDto);
        				this.putHeader(JwtUtil.JWT_KEY, jwtInfo);
        				for (Map.Entry<String, String> headerEntry : httpResponse.headers().entries()) {
        					this.putHeader(headerEntry.getKey(),headerEntry.getValue());
        				}
            			this.setResult(ResultUtil.success(token).toJSONString());
            			this.setStatus(HttpResponseStatus.OK);
            			return true;
        			}
        		}
        	}
    	} catch (Exception ex) {
    		logger.error("ERROR:",ex);
    	}

        return false;
    }
    
	
}
