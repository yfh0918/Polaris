package com.polaris.demo.gateway.request;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.polaris.container.gateway.pojo.HttpFile;
import com.polaris.container.gateway.pojo.HttpFilterMessage;
import com.polaris.container.gateway.request.HttpRequestFilter;
import com.polaris.container.gateway.util.FileReaderUtil;
import com.polaris.core.Constant;
import com.polaris.core.config.ConfClient;
import com.polaris.core.pojo.KeyValuePair;
import com.polaris.core.util.JwtUtil;
import com.polaris.core.util.PropertyUtil;
import com.polaris.core.util.ResultUtil;
import com.polaris.core.util.StringUtil;
import com.polaris.core.util.SystemCallUtil;
import com.polaris.core.util.UuidUtil;

import cn.hutool.core.util.StrUtil;
import io.jsonwebtoken.Claims;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;

/**
 * @author:Tom.Yu <p>
 * Description:
 * <p>
 * Token拦截
 */
public class HttpTokenRequestFilter extends HttpRequestFilter {

	private static String TOKEN_POLICY_DEFAULT_VALUE = "uncheck";//#request存在token 并且属于UNCHECKED_PATHS的url,如果policy=check就检查token的有效性，如果uncheck就不检查
	private static String TOKEN_POLICY = TOKEN_POLICY_DEFAULT_VALUE;
	public volatile static Set<String> UNCHECKED_PATHS = new HashSet<>();
	public volatile static Set<String> UNCHECKED_PATHS_PREFIX = new HashSet<>();
	public static String TOKEN_MESSAGE_CODE_DEFAULT_VALUE=Constant.TOKEN_FAIL_CODE;
	public static String TOKEN_MESSAGE_CODE=TOKEN_MESSAGE_CODE_DEFAULT_VALUE;
	public static String TOKEN_MESSAGE_DEFAULT_VALUE="认证失败，请先登录";
	public static String TOKEN_MESSAGE=TOKEN_MESSAGE_DEFAULT_VALUE;

	@Override
	public void onChange(HttpFile file) {
    	Set<String> UNCHECKED_PATHS_TEMP = new HashSet<>();
    	Set<String> UNCHECKED_PATHS_PREFIX_TEMP = new HashSet<>();
    	String TOKEN_MESSAGE_CODE_TEMP = null;
    	String TOKEN_MESSAGE_TEMP = null;
    	String TOKEN_POLICY_TEMP = null;
    	for (String conf : FileReaderUtil.getDataSet(file.getData())) {
			KeyValuePair kv = PropertyUtil.getKVPair(conf);
			if (kv != null && StringUtil.isNotEmpty(kv.getValue())) {
				// 不需要验证token的uri
    			if (kv.getKey().equals("UNCHECKED_PATHS")) {
    				UNCHECKED_PATHS_TEMP.add(kv.getValue());
    			}
    			
    			// 不需要验证token的uri前缀，一般为context
    			if (kv.getKey().equals("UNCHECKED_PATHS_PREFIX")) {
    				UNCHECKED_PATHS_PREFIX_TEMP.add(kv.getValue());
    			}
    			
    			//tokenMessageCode
    			if (kv.getKey().equals("TOKEN_MESSAGE_CODE")) {
    				TOKEN_MESSAGE_CODE_TEMP = kv.getValue();
    			}
    			
    			//tokenMessageCode
    			if (kv.getKey().equals("TOKEN_MESSAGE")) {
    				TOKEN_MESSAGE_TEMP = kv.getValue();
    			}
    			
    			//TOKEN_POLICY
    			if (kv.getKey().equals("TOKEN_POLICY")) {
					TOKEN_POLICY_TEMP = kv.getValue();
    			}
			}
    	}

    	UNCHECKED_PATHS_PREFIX = UNCHECKED_PATHS_PREFIX_TEMP;
    	UNCHECKED_PATHS = UNCHECKED_PATHS_TEMP;
    	if (TOKEN_MESSAGE_CODE_TEMP != null) {
    		TOKEN_MESSAGE_CODE = TOKEN_MESSAGE_CODE_TEMP;
    	} else {
    		TOKEN_MESSAGE_CODE = TOKEN_MESSAGE_CODE_DEFAULT_VALUE;
    	}
    	if (TOKEN_MESSAGE_TEMP != null) {
    		TOKEN_MESSAGE = TOKEN_MESSAGE_TEMP;
    	} else {
    		TOKEN_MESSAGE = TOKEN_MESSAGE_DEFAULT_VALUE;
    	}
    	if (TOKEN_POLICY_TEMP != null) {
    		TOKEN_POLICY = TOKEN_POLICY_TEMP;
    	} else {
    		TOKEN_POLICY = TOKEN_POLICY_DEFAULT_VALUE;
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
    	String value = httpRequest.headers().get(SystemCallUtil.key(ConfClient.get()));
    	return SystemCallUtil.verify(ConfClient.get(),value);
    }

    
    public static boolean isUncheckPolicy() {
    	if (TOKEN_POLICY.equals(TOKEN_POLICY_DEFAULT_VALUE)) {
    		return true;
    	}
    	return false;
    }
    
	@Override
    public boolean doFilter(HttpRequest originalRequest,HttpObject httpObject, HttpFilterMessage httpMessage) {
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
            	    httpRequest.headers().add(SystemCallUtil.key(ConfClient.get()), SystemCallUtil.value(ConfClient.get()));
            		return false;
            	}
            }
            
            //认证
            String token = httpRequest.headers().get("X-Admin-TokenID");
            //httpRequest.headers().set(Constant.TOKEN_ID, Constant.TOKEN_ID);

            //没有token需要验证url是否放过
            if (StringUtil.isEmpty(token)) {
                if (uncheckUrl) {
                    httpRequest.headers().add(SystemCallUtil.key(ConfClient.get()), SystemCallUtil.value(ConfClient.get()));
                	return false;
                }
                httpMessage.setResult(ResultUtil.create(TOKEN_MESSAGE_CODE,TOKEN_MESSAGE).toJSONString());
                return true;
            }

            try {
            	
            	//token认证
                String signKey = ConfClient.get(JwtUtil.JWT_SIGN_KEY, UuidUtil.generateUuid());
                Claims claims = JwtUtil.parseJWT(token,signKey);
                if (claims == null) {
                	httpMessage.setResult(ResultUtil.create(TOKEN_MESSAGE_CODE,TOKEN_MESSAGE).toJSONString());
                    return true;
                }
                String userName = claims.getSubject();
                if (StrUtil.isEmpty(userName)) {
                	httpMessage.setResult(ResultUtil.create(TOKEN_MESSAGE_CODE,TOKEN_MESSAGE).toJSONString());
                    return true;
                }
                
                //设置claims信息
                httpRequest.headers().add(JwtUtil.CLAIMS_KEY, JwtUtil.encode(claims));
                httpRequest.headers().add(SystemCallUtil.key(ConfClient.get()), SystemCallUtil.value(ConfClient.get()));
                return false;
            } catch (Exception ex) {
            	httpMessage.setResult(ResultUtil.create(TOKEN_MESSAGE_CODE,TOKEN_MESSAGE).toJSONString());
                return true;
            }

        }
        return false;
    }
}

