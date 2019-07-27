package com.polaris.gateway.request;

import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.polaris.core.util.LogUtil;
import com.polaris.gateway.GatewayConstant;
import com.polaris.gateway.util.ConfUtil;
import com.polaris.gateway.util.RequestUtil;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;

/**
 * @author:Tom.Yu
 * <p>
 * Description:
 * <p>
 * URL参数黑名单参数拦截
 */
@Service
public class ArgsHttpRequestFilter extends HttpRequestFilter {
	private static LogUtil logger = LogUtil.getInstance(ArgsHttpRequestFilter.class);

    @Override
    public boolean doFilter(HttpRequest originalRequest, HttpObject httpObject, ChannelHandlerContext channelHandlerContext) {
        if (httpObject instanceof HttpRequest) {
            logger.debug("filter:{}", this.getClass().getName());
            HttpRequest httpRequest = (HttpRequest) httpObject;
            String url = null;
            try {
                String uri=httpRequest.uri().replaceAll("%","%25");
                url = URLDecoder.decode(uri, "UTF-8");
            } catch (Exception e) {
                logger.warn("URL:{} is inconsistent with the rules", httpRequest.uri(), e);
            }
            if (url != null) {
                int index = url.indexOf("?");
                if (index > -1) {
                    String argsStr = url.substring(index + 1);
                    String[] args = argsStr.split("&");
                    for (String arg : args) {
                        String[] kv = arg.split("=");
                        if (kv.length == 2) {
                        	RequestUtil.setQueryString(kv[0].trim(), kv[1].trim());
                            for (Pattern pat : ConfUtil.getPattern(FilterType.ARGS.name())) {
                                Matcher matcher = pat.matcher(kv[1].toLowerCase());
                                if (matcher.find()) {
                                    hackLog(logger, GatewayConstant.getRealIp(httpRequest), FilterType.ARGS.name(), pat.toString());
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
}

