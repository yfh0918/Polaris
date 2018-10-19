package com.polaris.gateway.request;

import org.springframework.stereotype.Service;

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


    @Override
    public boolean doFilter(HttpRequest originalRequest, HttpObject httpObject, ChannelHandlerContext channelHandlerContext) {
//        if (httpObject instanceof HttpRequest) {
//        	
//            //获取request
//            HttpRequest httpRequest = (HttpRequest) httpObject;
//
//            //获取url
//            String uri = httpRequest.uri();
//
//            //必须检查的路径
//            for (int i0 = 0; i0 < CHECK_PATHS.size(); i0++) {
//                if (!uri.startsWith(CHECK_PATHS.get(i0))) {
//                    return false;
//                }
//            }
//
//            //放过
//            String url;
//            int index = uri.indexOf("?");
//            if (index > 0) {
//                url = uri.substring(0, index);
//            } else {
//                url = uri;
//            }
//            if (UNCHECKED_PATHS.contains(url)) {
//                return false;
//            }
//            for (int i0 = 0; i0 < UNCHECKED_PATHS_PREFIX.size(); i0++) {
//                if (url.startsWith(UNCHECKED_PATHS_PREFIX.get(i0))) {
//                    return false;
//                }
//            }
//            for (int i0 = 0; i0 < UNCHECKED_PATHS_CONTAIN.size(); i0++) {
//                if (url.contains(UNCHECKED_PATHS_CONTAIN.get(i0))) {
//                    return false;
//                }
//            }
//
//            //从Cookie中取Token
//            String token = RequestUtil.getCookie(CodeConstants.MW_TOKEN);
//            if (SysUtils.isObjectNull(token)) {
//                redirectLogin(httpRequest);
//                return false;
//            }
//
//            // 当前用户已登录，放行
//            String userInfo = AccessTokenUtil.getUserInfoByToken(token);
//            if (StringUtil.isEmpty(userInfo)) {
//                redirectLogin(httpRequest);
//                return false;
//            }
//
//            // 用户信息放入header
//    		httpRequest.headers().set("user", userInfo);
//
//            //重复提交(验证key)
//            for (int i0 = 0; i0 < REPEATE_SUBMIT_CHECK_PATH.size(); i0++) {
//                if (uri.startsWith(REPEATE_SUBMIT_CHECK_PATH.get(0))) {
//                    String serverToken = RedisUtil.get(token);
//                    if (StringUtil.isNotEmpty(serverToken)) {
//                        logger.info("uri:{},user:{},cause:{}", REPEATE_SUBMIT_CHECK_PATH.get(0), userInfo, "重复提交");
//                        return true;
//                    }
//                    RedisUtil.set(token, UuidUtil.generateUuid(), REPEATE_SUBMIT_SECONDES);
//                    break;
//                }
//            }
//        }
//
        return false;
    }

//    private void redirectLogin(HttpRequest httpRequest) {
//        String baseUrl = "/mwclgcommerce/user/getLoginPage";
//        boolean isEnglish = false;
//        String url = httpRequest.uri();
//
//        //是否英文
//        if (url.contains("/" + CodeConstants.LANG_EN + "/")) {
//            isEnglish = true;
//        }
//
//        //英文版改版 增加临时判断
//        if (("en").equals(RequestUtil.getQueryString("lang"))) {
//            isEnglish = true;
//
//        }
//        if (isEnglish) {
//            baseUrl = "/mwclgcommerce/user/getLoginPage?lang=en";
//        }
//        httpRequest.setUri(baseUrl);
//    }
}

