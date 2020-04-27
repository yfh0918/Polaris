package com.polaris.container.gateway.request;

import java.net.URLDecoder;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.container.gateway.HttpFilterConstant;
import com.polaris.container.gateway.pojo.HttpFilterFile;
import com.polaris.container.gateway.util.RequestUtil;

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
public class HttpArgsRequestFilter extends HttpRequestFilter {
	private static Logger logger = LoggerFactory.getLogger(HttpArgsRequestFilter.class);
	private Set<Pattern> patterns = new HashSet<>();

	@Override
	public void onChange(HttpFilterFile file) {
		Set<Pattern> tempPatterns = new HashSet<>();
		for (String conf : file.getData()) {
			tempPatterns.add(Pattern.compile(conf));
		}
		patterns = tempPatterns;
	}
	
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
                            for (Pattern pat : patterns) {
                                Matcher matcher = pat.matcher(kv[1].toLowerCase());
                                if (matcher.find()) {
                                    hackLog(logger, HttpFilterConstant.getRealIp(httpRequest), HttpArgsRequestFilter.class.getSimpleName(), pat.toString());
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

