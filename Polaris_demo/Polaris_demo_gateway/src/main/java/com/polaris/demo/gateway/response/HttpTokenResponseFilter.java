package com.polaris.demo.gateway.response;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.polaris.container.gateway.pojo.HttpFilterMessage;
import com.polaris.container.gateway.response.HttpResponseFilter;
import com.polaris.core.config.ConfClient;
import com.polaris.core.util.JwtUtil;
import com.polaris.core.util.ResultUtil;
import com.polaris.core.util.StringUtil;
import com.polaris.core.util.UuidUtil;

import io.netty.handler.codec.http.HttpObject;
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
    public boolean doFilter(HttpResponse originalResponse, HttpObject httpObject, HttpFilterMessage httpMessage) {
    	if (httpObject instanceof HttpResponse) {
    	    HttpResponse httpResponse = (HttpResponse)httpObject;
            try {
                String jwtInfo = httpResponse.headers().get(JwtUtil.JWT_KEY);
                if (StringUtil.isNotEmpty(jwtInfo)) {
                    Map<String, Object> user =  JSONObject.parseObject(JwtUtil.decode(jwtInfo));
                    if (user != null) {
                        long ttlMillis = Long.parseLong(user.remove(JwtUtil.JWT_TTL_MILLIS_KEY).toString());
                        String signKey = ConfClient.get(JwtUtil.JWT_SIGN_KEY, UuidUtil.generateUuid());
                        String token = JwtUtil.createJWT(ttlMillis, signKey, user);
                        httpMessage.putHeader(JwtUtil.JWT_KEY, jwtInfo);
                        for (Map.Entry<String, String> headerEntry : httpResponse.headers().entries()) {
                            httpMessage.putHeader(headerEntry.getKey(),headerEntry.getValue());
                        }
                        httpMessage.setResult(ResultUtil.success(token).toJSONString());
                        httpMessage.setStatus(HttpResponseStatus.OK);
                        return true;
                    }
                }
            } catch (Exception ex) {
                logger.error("ERROR:",ex);
            }
    	}
        return false;
    }
    
	
}
