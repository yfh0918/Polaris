package com.polaris.gateway.request;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.polaris.core.Constant;
import com.polaris.core.config.ConfClient;
import com.polaris.core.config.ConfHandlerSupport;
import com.polaris.core.config.ConfListener;
import com.polaris.core.util.JwtUtil;
import com.polaris.core.util.StringUtil;
import com.polaris.gateway.support.HttpRequestFilterSupport;

import cn.hutool.core.util.StrUtil;
import io.jsonwebtoken.Claims;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;

/**
 * @author:Tom.Yu <p>
 * Description:
 * <p>
 * Token拦截
 */
@Service
public class TokenHttpRequestFilter extends HttpRequestFilter {

	public volatile static Set<String> UNCHECKED_PATHS = new HashSet<>();
	public volatile static Set<String> UNCHECKED_PATHS_PREFIX = new HashSet<>();
	private final static String FILE_NAME = "token.txt";
	private final static String TOKEN_MESSAGE="认证失败，请先登录";

	static {
		//先获取
		loadFile(ConfClient.getConfigValue(FILE_NAME));
		
		//后监听
    	ConfClient.addListener(FILE_NAME, new ConfListener() {
			@Override
			public void receive(String content) {
				loadFile(content);
			}
    	});
    }
    
    private static void loadFile(String content) {
    	if (StringUtil.isEmpty(content)) {
    		UNCHECKED_PATHS = new HashSet<>();
    		UNCHECKED_PATHS_PREFIX = new HashSet<>();
    		return;
    	}
    	String[] contents = content.split(Constant.LINE_SEP);
    	Set<String> UNCHECKED_PATHS_TEMP = new HashSet<>();
    	Set<String> UNCHECKED_PATHS_PREFIX_TEMP = new HashSet<>();
 
    	for (String conf : contents) {
    		if (StringUtil.isNotEmpty(conf) && !conf.startsWith("#")) {
    			conf = conf.replace("\n", "");
    			conf = conf.replace("\r", "");

				String[] kv = ConfHandlerSupport.getKeyValue(conf);

				// 不需要验证token的uri
    			if (kv[0].equals("UNCHECKED_PATHS")) {
    				UNCHECKED_PATHS_TEMP.add(kv[1]);
    			}
    			
    			// 不需要验证token的uri前缀，一般为context
    			if (kv[0].equals("UNCHECKED_PATHS_PREFIX")) {
    				UNCHECKED_PATHS_PREFIX_TEMP.add(kv[1]);
    			}
    		}
    	}
    	UNCHECKED_PATHS_PREFIX = UNCHECKED_PATHS_PREFIX_TEMP;
    	UNCHECKED_PATHS = UNCHECKED_PATHS_TEMP;
    }
    
    //验证url
    public static boolean checkUrlPath(HttpRequest httpRequest) {
    	//获取url
        String uri = httpRequest.uri();
        String url;
        int index = uri.indexOf("?");
        if (index > 0) {
            url = uri.substring(0, index);
        } else {
            url = uri;
        }
        for (String context : UNCHECKED_PATHS_PREFIX) {
        	if (url.startsWith(context)) {
        		return false;
        	}
        }
        if (UNCHECKED_PATHS.contains(url)) {
        	return false;
        }
        return true;
    }
    
	@Override
    public boolean doFilter(HttpRequest originalRequest, HttpObject httpObject, ChannelHandlerContext channelHandlerContext) {
        if (httpObject instanceof HttpRequest) {
        	
            //获取request
            HttpRequest httpRequest = (HttpRequest) httpObject;

            //验证url
            boolean checkResult = checkUrlPath(httpRequest);
            if (!checkResult) {
            	return false;
            }

            //认证
            String token = httpRequest.headers().get(Constant.TOKEN_ID);
            try {
                if (StringUtil.isEmpty(token)) {
                	this.setResultDto(HttpRequestFilterSupport.createResultDto(Constant.TOKEN_FAIL_CODE,TOKEN_MESSAGE));
                    return true;
                }
                Claims claims = JwtUtil.parseJWT(token);
                if (claims == null) {
                	this.setResultDto(HttpRequestFilterSupport.createResultDto(Constant.TOKEN_FAIL_CODE,TOKEN_MESSAGE));
                    return true;
                }
                String userName = claims.getSubject();
                if (StrUtil.isEmpty(userName)) {
                	this.setResultDto(HttpRequestFilterSupport.createResultDto(Constant.TOKEN_FAIL_CODE,TOKEN_MESSAGE));
                    return true;
                }
                return false;
            } catch (Exception ex) {
            	this.setResultDto(HttpRequestFilterSupport.createResultDto(Constant.TOKEN_FAIL_CODE,TOKEN_MESSAGE));
                return true;
            }

        }
        return false;
    }
}

