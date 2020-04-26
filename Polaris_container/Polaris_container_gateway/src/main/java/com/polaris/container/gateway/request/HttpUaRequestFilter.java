package com.polaris.container.gateway.request;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.container.gateway.HttpFilterConstant;
import com.polaris.container.gateway.pojo.HttpFilterFile;

import io.netty.channel.ChannelHandlerContext;
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
	public void onChange(HttpFilterFile file) {
		Set<String> data = file.getData();
		Set<Pattern> tempPatterns = new HashSet<>();
		if (data != null) {
			for (String conf : data) {
				tempPatterns.add(Pattern.compile(conf));
			}
		}
		patterns = tempPatterns;
	}
	
    @Override
    public boolean doFilter(HttpRequest originalRequest, HttpObject httpObject, ChannelHandlerContext channelHandlerContext) {
        if (httpObject instanceof HttpRequest) {
            logger.debug("filter:{}", this.getClass().getName());
            HttpRequest httpRequest = (HttpRequest) httpObject;
            List<String> headerValues = HttpFilterConstant.getHeaderValues(originalRequest, "User-Agent");
            if (headerValues.size() > 0 && headerValues.get(0) != null) {
                for (Pattern pat : patterns) {
                    Matcher matcher = pat.matcher(headerValues.get(0));
                    if (matcher.find()) {
                        hackLog(logger, HttpFilterConstant.getRealIp(httpRequest), HttpUaRequestFilter.class.getSimpleName(), pat.toString());
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
