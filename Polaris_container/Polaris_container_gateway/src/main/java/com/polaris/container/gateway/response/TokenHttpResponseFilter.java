package com.polaris.container.gateway.response;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.polaris.core.Constant;
import com.polaris.core.util.JwtUtil;
import com.polaris.core.util.StringUtil;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.util.CharsetUtil;

/**
 * @author:Tom.Yu
 *
 * Description:
 *
 */
@Service
public class TokenHttpResponseFilter extends HttpResponseFilter {
	
    @Override
    public HttpResponse doFilter(HttpRequest originalRequest, HttpResponse httpResponse) {
    	String tokenBody = httpResponse.headers().get(Constant.TOKEN_KEY);
    	if (StringUtil.isNotEmpty(tokenBody)) {
    		try {
    			JSONObject jsonObject = JSONObject.parseObject(tokenBody);
    			long tokenTime = jsonObject.getLong(Constant.TOKEN_TTL_MILLIS);
    			String token = JwtUtil.createJWT(tokenTime, jsonObject);
    			if (httpResponse instanceof DefaultFullHttpResponse) {
        			ByteBuf content = io.netty.buffer.Unpooled.copiedBuffer(token, CharsetUtil.UTF_8); 
        			httpResponse = ((DefaultFullHttpResponse)httpResponse).replace(content);
    			}            	
    			httpResponse.headers().remove(Constant.TOKEN_KEY);
    		} catch (Exception ex) {}
    	}
        return httpResponse;
    }
}
