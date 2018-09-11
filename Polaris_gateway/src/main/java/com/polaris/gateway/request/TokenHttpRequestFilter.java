package com.polaris.gateway.request;

import org.springframework.stereotype.Service;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;

/**
 * @author:Tom.Yu
 * <p>
 * Description:
 * <p>
 * Token拦截
 */
@Service
public class TokenHttpRequestFilter extends HttpRequestFilter {
//	private static String AUTH_ERROR = "认证失败,请重新登陆！";
	
//    @Reference(version = "1.0.0")
//    private AuthService authService;

    @Override
    public boolean doFilter(HttpRequest originalRequest, HttpObject httpObject, ChannelHandlerContext channelHandlerContext) {
    	
//    	if (httpObject instanceof HttpRequest) {
//    		
//    		HttpRequest httpRequest = (HttpRequest) httpObject;
//        	String[] includePathPatterns = ConfClient.get("auth.filter.includePathPatterns", "").split("\\|");
//        	String[] excludePathPatterns = ConfClient.get("auth.filter.excludePathPatterns", "").split("\\|");
//    		
//        	//support CORS（服务器跨域请求），浏览器的自发行为不会带token，直接跳过验证
//            List<String> originHeader = GatewayConstant.getHeaderValues(originalRequest, "Origin");
//            if (originHeader.size() > 0) {
//               return false;
//            }
//            
//            //后续处理
//      		boolean isExist = false;
//      		String uri = originalRequest.uri();
//      		for (String excludePathPattern : excludePathPatterns) {
//      			if (uri.toLowerCase().contains(excludePathPattern.toLowerCase())) {
//      				isExist = true;
//      				break;
//      			}
//      		}
//      		if (!isExist) {
//      			
//      			//是否在包含数组中
//      			for (String includePathPattern : includePathPatterns) {
//      				if (uri.toLowerCase().contains(includePathPattern.toLowerCase())) {
//      					
//      			        //获取token和token类型
//      			        List<String> tokenList = GatewayConstant.getHeaderValues(httpRequest, Constant.USER_TOKEN);
//      			        List<String> typeList = GatewayConstant.getHeaderValues(httpRequest, Constant.REQUEST_TYPE);
//      			        String token = tokenList.size() == 0 ? null : tokenList.get(0);
//      			        String type = typeList.size() == 0 ? null : typeList.get(0);
//      			        
//      			        //没有type和token，传统的登陆方式直接放过
//      			        if (StringUtil.isEmpty(type) && StringUtil.isEmpty(token)) {
//      			        	return false;
//      			        }
//      			        
//      			        //参数不一致直接返回错误
//      			        if (StringUtil.isNotEmpty(type) && 
//      			        		!Constant.LOGIN_USER_TYPE.equals(type) &&
//      			        		!Constant.TOKEN_USER_TYPE.equals(type)) {
//      			        	this.setResultDto(
//      			        			HttpRequestFilterSupport.createResultDto(
//      			        					Constant.STATUS_AUTHENTICATION_FAILED,
//      			        					AUTH_ERROR));
//      			        	return true;
//      			        }
//      			        
//      			        //不存在token直接返回错误
//      			        if (StringUtil.isEmpty(token)) {
//      			        	this.setResultDto(
//      			        			HttpRequestFilterSupport.createResultDto(
//      			        					Constant.STATUS_AUTHENTICATION_FAILED,
//      			        					AUTH_ERROR));
//      			        	return true;
//      			        }
//      			        
//      			        //参数
//      					TokenDto tokenDto = new TokenDto();
//  	  					tokenDto.setRequestType(type);
//      					tokenDto.setToken(token);
//      					tokenDto.setUri(originalRequest.uri());
//
//      	  				//非登陆用户
//      					String result = null;
//      					if (!Constant.LOGIN_USER_TYPE.equals(type)) {
//      						if(StringUtil.isEmpty(type)) {
//      							tokenDto.setIp_addr(GatewayConstant.getRealIp(originalRequest, channelHandlerContext));//外部token用户需要验证IP地址
//      						} 
//      						long expireTime = authService.getExpireTime(tokenDto);
//      						if (expireTime > 0) {
//      							result = tokenDto.getToken();
//      						}
//      					} else {
//      						
//      						//登陆用户
//      						result = authService.validateToken(tokenDto);
//      					}
//      	  				
//      					//验证结果
//      					if(StringUtil.isEmpty(result)){
//      						this.setResultDto(
//      			        			HttpRequestFilterSupport.createResultDto(
//      			        					Constant.STATUS_AUTHENTICATION_FAILED,
//      			        					AUTH_ERROR));
//      			        	return true;
//      					} else {
//      						if(Constant.LOGIN_USER_TYPE.equals(type)){
//      							String yh_id = result.substring(0, result.indexOf(Constant.SESSION_YH_ID_MC_SPLIT));
//      							String yh_mc = result.substring(result.indexOf(Constant.SESSION_YH_ID_MC_SPLIT)+Constant.SESSION_YH_ID_MC_SPLIT.length() + 1);
//      							httpRequest.headers().set(Constant.SESSION_YH_ID, yh_id);
//      							httpRequest.headers().set(Constant.SESSION_USER_ID, yh_mc);
//      						}
//
//      					}
//      	  				break;
//      				}
//      			}
//      		}
//    	}
    	return false;
    }
}

