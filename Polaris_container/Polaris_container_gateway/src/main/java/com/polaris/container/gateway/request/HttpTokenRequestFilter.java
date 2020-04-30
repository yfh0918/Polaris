package com.polaris.container.gateway.request;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.polaris.container.gateway.pojo.HttpFilterFile;
import com.polaris.core.Constant;
import com.polaris.core.pojo.KeyValuePair;
import com.polaris.core.util.JwtUtil;
import com.polaris.core.util.PropertyUtil;
import com.polaris.core.util.ResultUtil;
import com.polaris.core.util.StringUtil;
import com.polaris.core.util.SystemCallUtil;

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
public class HttpTokenRequestFilter extends HttpRequestFilter {

	private static String TOKEN_POLICY_UNCHECK = "uncheck";//#request存在token 并且属于UNCHECKED_PATHS的url,如果policy=check就检查token的有效性，如果uncheck就不检查
	private static String TOKEN_POLICY = TOKEN_POLICY_UNCHECK;
	public volatile static Set<String> UNCHECKED_PATHS = new HashSet<>();
	public volatile static Set<String> UNCHECKED_PATHS_PREFIX = new HashSet<>();
	public volatile static Set<String> TOKEN_PATHS = new HashSet<>();
	public static String TOKEN_MESSAGE_CODE=Constant.TOKEN_FAIL_CODE;
	public static String TOKEN_MESSAGE="认证失败，请先登录";
	public final static String DEFAULT_VALUE = "1";

	@Override
	public void onChange(HttpFilterFile file) {
    	Set<String> UNCHECKED_PATHS_TEMP = new HashSet<>();
    	Set<String> UNCHECKED_PATHS_PREFIX_TEMP = new HashSet<>();
    	Set<String> TOKEN_PATHS_TEMP = new HashSet<>();
    	String TOKEN_MESSAGE_CODE_TEMP = null;
    	String TOKEN_MESSAGE_TEMP = null;
    	String TOKEN_POLICY_TEMP = null;
    	for (String conf : file.getData()) {
			KeyValuePair kv = PropertyUtil.getKVPair(conf);
			if (kv != null) {
				// 不需要验证token的uri
    			if (kv.getKey().equals("UNCHECKED_PATHS")) {
    				if (StringUtil.isNotEmpty(kv.getValue())) {
        				UNCHECKED_PATHS_TEMP.add(kv.getValue());
    				}
    			}
    			
    			// 不需要验证token的uri前缀，一般为context
    			if (kv.getKey().equals("UNCHECKED_PATHS_PREFIX")) {
    				if (StringUtil.isNotEmpty(kv.getValue())) {
        				UNCHECKED_PATHS_PREFIX_TEMP.add(kv.getValue());
    				}
    			}
    			
    			//tokenUrl
    			if (kv.getKey().equals("TOKEN_PATH") || kv.getKey().equals("TOKEN_PATHS")) {
    				if (StringUtil.isNotEmpty(kv.getValue())) {
        				TOKEN_PATHS_TEMP.add(kv.getValue());
    				}
    			}
    			
    			//tokenMessageCode
    			if (kv.getKey().equals("TOKEN_MESSAGE_CODE")) {
    				if (StringUtil.isNotEmpty(kv.getValue())) {
        				TOKEN_MESSAGE_CODE_TEMP = kv.getValue();
    				}
    			}
    			
    			//tokenMessageCode
    			if (kv.getKey().equals("TOKEN_MESSAGE")) {
    				if (StringUtil.isNotEmpty(kv.getValue())) {
        				TOKEN_MESSAGE_TEMP = kv.getValue();
    				}
    			}
    			
    			//TOKEN_POLICY
    			if (kv.getKey().equals("TOKEN_POLICY")) {
    				if (StringUtil.isNotEmpty(kv.getValue())) {
    					TOKEN_POLICY_TEMP = kv.getValue();
    				}
    			}
			}
    	}

    	UNCHECKED_PATHS_PREFIX = UNCHECKED_PATHS_PREFIX_TEMP;
    	UNCHECKED_PATHS = UNCHECKED_PATHS_TEMP;
    	TOKEN_PATHS = TOKEN_PATHS_TEMP;
    	if (TOKEN_MESSAGE_CODE_TEMP != null) {
    		TOKEN_MESSAGE_CODE = TOKEN_MESSAGE_CODE_TEMP;
    	}
    	if (TOKEN_MESSAGE_TEMP != null) {
    		TOKEN_MESSAGE = TOKEN_MESSAGE_TEMP;
    	}
    	if (TOKEN_POLICY_TEMP != null) {
    		TOKEN_POLICY = TOKEN_POLICY_TEMP;
    	}
    }
    
    //获取url
    public static String getUrl(HttpRequest httpRequest) {
        return getUrl(httpRequest, null);
    }
    public static String getUrl(HttpRequest httpRequest, Map<String, String> parameterMap) {
    	//获取url
        String uri = httpRequest.uri();
        String url;
        int index = uri.indexOf("?");
        if (index > 0) {
            url = uri.substring(0, index);
            if (parameterMap != null) {
            	String strParameter = uri.substring(index + 1);
            	String[] parameters = strParameter.split("&");
            	for (String parameter : parameters) {
            		KeyValuePair kv = PropertyUtil.getKVPair(parameter);
            		parameterMap.put(kv.getKey(), kv.getValue());
            	}
            }
        } else {
            url = uri;
        }
        return url;
    }
    
    //验证url
    public static boolean checkUrlPath(String url) {
        
        //验证是否需要放过
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
    
    //验证url
    public static boolean isSystemCall(HttpRequest httpRequest) {
    	String value = httpRequest.headers().get(SystemCallUtil.key());
    	return SystemCallUtil.verify(value);
    }

    
    public static boolean isTokenPath(String url) {
    	return TOKEN_PATHS.contains(url);
    }
    
    public static boolean isUncheckPolicy() {
    	if (TOKEN_POLICY.equals(TOKEN_POLICY_UNCHECK)) {
    		return true;
    	}
    	return false;
    }
    
	@Override
    public boolean doFilter(HttpRequest originalRequest, HttpObject httpObject, ChannelHandlerContext channelHandlerContext) {
        if (httpObject instanceof HttpRequest) {
        	
            //获取request
            HttpRequest httpRequest = (HttpRequest) httpObject;
            
            //检验系统调用
            if (isSystemCall(httpRequest)) {
                return false;
            }

            //是否为不验证的url
            boolean uncheckUrl = !checkUrlPath(HttpTokenRequestFilter.getUrl(httpRequest));
            if (uncheckUrl) {
            	if (isUncheckPolicy()) {
            		return false;
            	}
            }
            
            //认证
            String token = httpRequest.headers().get(Constant.TOKEN_ID);
            httpRequest.headers().set(Constant.TOKEN_ID, DEFAULT_VALUE);

            //没有token需要验证url是否放过
            if (StringUtil.isEmpty(token)) {
                if (uncheckUrl) {
                	return false;
                }
            	this.setResult(ResultUtil.create(TOKEN_MESSAGE_CODE,TOKEN_MESSAGE).toJSONString());
                return true;
            }

            try {
            	
            	//token认证
                Claims claims = JwtUtil.parseJWT(token);
                if (claims == null) {
                	this.setResult(ResultUtil.create(TOKEN_MESSAGE_CODE,TOKEN_MESSAGE).toJSONString());
                    return true;
                }
                String userName = claims.getSubject();
                if (StrUtil.isEmpty(userName)) {
                	this.setResult(ResultUtil.create(TOKEN_MESSAGE_CODE,TOKEN_MESSAGE).toJSONString());
                    return true;
                }
                
                //设置claims信息
                httpRequest.headers().add(JwtUtil.CLAIMS_KEY, JwtUtil.encode(claims));
                httpRequest.headers().add(SystemCallUtil.key(), SystemCallUtil.value());
                return false;
            } catch (Exception ex) {
            	this.setResult(ResultUtil.create(TOKEN_MESSAGE_CODE,TOKEN_MESSAGE).toJSONString());
                return true;
            }

        }
        return false;
    }
}

