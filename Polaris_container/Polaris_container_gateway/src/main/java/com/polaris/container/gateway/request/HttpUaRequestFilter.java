package com.polaris.container.gateway.request;

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
import com.polaris.container.gateway.util.FileReaderUtil;

import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;

/**
 * @author:Tom.Yu
 *
 * Description:
 *
 */
public class HttpUaRequestFilter extends HttpRequestFilter {
	private static Logger logger = LoggerFactory.getLogger(HttpUaRequestFilter.class);

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
    public HttpFilterMessage doFilter(HttpRequest originalRequest,HttpObject httpObject) {
        if (httpObject instanceof HttpRequest) {
            logger.debug("filter:{}", this.getClass().getName());
            HttpRequest httpRequest = (HttpRequest) httpObject;
            List<String> headerValues = HttpConstant.getHeaderValues(((HttpRequest)httpObject), "User-Agent");
            if (headerValues.size() > 0 && headerValues.get(0) != null) {
                for (Pattern pat : patterns) {
                    Matcher matcher = pat.matcher(headerValues.get(0));
                    if (matcher.find()) {
                        hackLog(logger, HttpConstant.getRealIp(httpRequest), HttpUaRequestFilter.class.getSimpleName(), pat.toString());
                        return HttpFilterMessage.of("HttpUaRequestFilter Black List");
                    }
                }
            }
        }
        return null;
    }
}
