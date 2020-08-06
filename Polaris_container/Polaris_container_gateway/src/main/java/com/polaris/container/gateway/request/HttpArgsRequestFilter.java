package com.polaris.container.gateway.request;

import java.net.URLDecoder;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.container.gateway.HttpConstant;
import com.polaris.container.gateway.pojo.HttpFile;
import com.polaris.container.gateway.pojo.HttpFilterMessage;
import com.polaris.container.gateway.util.RequestUtil;
import com.polaris.core.pojo.KeyValuePair;
import com.polaris.core.pojo.ServerHost;

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
	public void onChange(HttpFile file) {
		Set<Pattern> tempPatterns = new HashSet<>();
		for (String conf : file.getData()) {
			tempPatterns.add(Pattern.compile(conf));
		}
		patterns = tempPatterns;
	}
	
    @Override
    public boolean doFilter(HttpRequest originalRequest, HttpObject httpObject, HttpFilterMessage httpMessage) {
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
                List<KeyValuePair> list = ServerHost.getKeyValuePairs(url);
                for (KeyValuePair kv : list) {
                    RequestUtil.setQueryString(kv.getKey(), kv.getValue());
                    for (Pattern pat : patterns) {
                        Matcher matcher = pat.matcher(kv.getValue().toLowerCase());
                        if (matcher.find()) {
                            hackLog(logger, HttpConstant.getRealIp(httpRequest), HttpArgsRequestFilter.class.getSimpleName(), pat.toString());
                            return true;
                        }
                    }
                }
                
            }
        }
        return false;
    }
}

