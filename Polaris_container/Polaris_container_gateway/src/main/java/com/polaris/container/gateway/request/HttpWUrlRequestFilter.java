package com.polaris.container.gateway.request;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.container.gateway.HttpConstant;
import com.polaris.container.gateway.pojo.HttpFile;
import com.polaris.container.gateway.pojo.HttpFilterMessage;
import com.polaris.container.gateway.util.FileReaderUtil;

import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * @author:Tom.Yu
 *
 * Description:
 *
 */
public class HttpWUrlRequestFilter extends HttpRequestFilter {
	private static Logger logger = LoggerFactory.getLogger(HttpWUrlRequestFilter.class);

	private Set<Pattern> patterns = new HashSet<>();

	@Override
	public void onChange(HttpFile file) {
		Set<Pattern> tempPatterns = new HashSet<>();
		for (String conf : FileReaderUtil.getDataSet(file.getData())) {
			tempPatterns.add(Pattern.compile(conf));
		}
		patterns = tempPatterns;
	}
	
    @Override
    public boolean isBlacklist() {
        return false;
    }

    @Override
    public boolean doFilter(HttpRequest originalRequest,HttpObject httpObject, HttpFilterMessage httpMessage) {
        if (httpObject instanceof HttpRequest) {
            logger.debug("filter:{}", this.getClass().getName());
            HttpRequest httpRequest = (HttpRequest) httpObject;
            String url;
            int index = httpRequest.uri().indexOf("?");
            if (index > -1) {
                url = httpRequest.uri().substring(0, index);
            } else {
                url = httpRequest.uri();
            }
            for (Pattern pat : patterns) {
                Matcher matcher = pat.matcher(url);
                if (matcher.find()) {
                    hackLog(logger, HttpConstant.getRealIp(httpRequest), HttpWUrlRequestFilter.class.getSimpleName(), pat.toString());
                    if (url.startsWith("/favicon.ico")) {
                    	httpMessage.setExit(true);
                    	httpMessage.setStatus(HttpResponseStatus.OK);
                    }
                    return true;
                }
            }
        }
        return false;
    }
}