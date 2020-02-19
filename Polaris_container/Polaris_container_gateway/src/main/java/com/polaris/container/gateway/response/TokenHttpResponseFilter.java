package com.polaris.container.gateway.response;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.polaris.container.gateway.request.TokenHttpRequestFilter;
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
@Service
public class TokenHttpResponseFilter extends HttpResponseFilter {
	private static Logger logger = LoggerFactory.getLogger(TokenHttpResponseFilter.class);
    @Override
    public boolean doFilter(HttpRequest originalRequest, HttpResponse httpResponse) {
    	try {
        	String url = TokenHttpRequestFilter.getUrl(originalRequest);
        	if (TokenHttpRequestFilter.isTokenPath(url)) {
        		String jwtInfo = httpResponse.headers().get(JwtUtil.JWT_KEY);
         		if (StringUtil.isNotEmpty(jwtInfo)) {
        			Map<String, Object> requestDto =  JSONObject.parseObject(JwtUtil.decode(jwtInfo));
        			if (requestDto != null) {
        				String token = JwtUtil.createJWT(requestDto);
            			this.setResultDto(ResultUtil.success(token));
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
