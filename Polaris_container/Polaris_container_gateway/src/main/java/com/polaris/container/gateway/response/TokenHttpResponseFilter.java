package com.polaris.container.gateway.response;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.polaris.container.gateway.request.TokenHttpRequestFilter;
import com.polaris.core.Constant;
import com.polaris.core.dto.ResultDto;
import com.polaris.core.util.JwtUtil;
import com.polaris.core.util.ResultUtil;
import com.polaris.core.util.StringUtil;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

/**
 * @author:Tom.Yu
 *
 * Description:
 *
 */
@SuppressWarnings({"rawtypes","unchecked"})
@Service
public class TokenHttpResponseFilter extends HttpResponseFilter {
	
    @Override
    public boolean doFilter(HttpRequest originalRequest, HttpResponse httpResponse) {
    	String url = TokenHttpRequestFilter.getUrl(originalRequest);
    	if (TokenHttpRequestFilter.isTokenPath(url)) {
    		if (httpResponse instanceof FullHttpResponse) {
    			if (convertByteBufToString(((FullHttpResponse)httpResponse).content())) {
        			return true;
    			}
    		}
    	}
        return false;
    }
    
	public boolean convertByteBufToString(ByteBuf buf) {
		try {
			String str;
	    	if(buf.hasArray()) { // 处理堆缓冲区
	    		str = new String(buf.array(), buf.arrayOffset() + buf.readerIndex(), buf.readableBytes());
	    	} else { // 处理直接缓冲区以及复合缓冲区
		    	byte[] bytes = new byte[buf.readableBytes()];
		    	buf.getBytes(buf.readerIndex(), bytes);
		    	str = new String(bytes, 0, buf.readableBytes());
	    	}
	    	if (StringUtil.isNotEmpty(str)) {
	    		ResultDto dto = JSON.parseObject(str, ResultDto.class);
	    		if (dto.getCode().equals(Constant.RESULT_SUCCESS)) {
	    			Map<String, Object> requestDto = (Map<String, Object>) dto.getData();
	    			if (requestDto != null) {
        				String token = JwtUtil.createJWT(requestDto);
            			this.setResultDto(ResultUtil.success(token));
            			return true;
	    			}
	    			
	    		}
	    	}
		} catch (Exception ex) {}
    	return false;
    }
}
