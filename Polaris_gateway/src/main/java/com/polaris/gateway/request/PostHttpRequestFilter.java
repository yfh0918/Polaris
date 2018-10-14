package com.polaris.gateway.request;

import java.net.URLDecoder;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.polaris.gateway.GatewayConstant;
import com.polaris.gateway.util.ConfUtil;
import com.polaris.gateway.util.RequestUtil;
import com.polaris.comm.util.LogUtil;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;

/**
 * @author:Tom.Yu
 *
 * Description:
 *
 */
@Service
public class PostHttpRequestFilter extends HttpRequestFilter {
	private static LogUtil logger = LogUtil.getInstance(PostHttpRequestFilter.class);
    private static Pattern filePattern = Pattern.compile("Content-Disposition: form-data;(.+)filename=\"(.+)\\.(.*)\"");

    @Override
    public boolean doFilter(HttpRequest originalRequest, HttpObject httpObject, ChannelHandlerContext ctx) {
        if (originalRequest.method().name().equals("POST")) {
            if (httpObject instanceof HttpContent) {
                HttpContent httpContent = (HttpContent) httpObject;
                String contentBody = null;
                List<String> headerValues = GatewayConstant.getHeaderValues(originalRequest, "Content-Type");
                if (headerValues.size() > 0 && headerValues.get(0) != null) {
                    if (GatewayConstant.getHeaderValues(originalRequest, "Content-Type") != null && headerValues.get(0).startsWith("multipart/form-data")) {
                        contentBody = new String(Unpooled.copiedBuffer(httpContent.content()).array());
                    } else {
                        try {
                            String contentStr = new String(Unpooled.copiedBuffer(httpContent.content()).array()).replaceAll("%", "%25");
                            contentBody = URLDecoder.decode(contentStr, "UTF-8");
                        } catch (Exception e) {
                            logger.warn("URL:{} POST body is inconsistent with the rules", originalRequest.uri(), e);
                        }
                    }

                    if (contentBody != null) {
                    	String[] kv = contentBody.split("=");
                        if (kv.length == 2) {
                        	RequestUtil.setPostParameter(kv[0].trim(), kv[1].trim());
                        }
                        List<Pattern> postPatternList = ConfUtil.getPattern(FilterType.POST.name());
                        for (Pattern pattern : postPatternList) {
                            Matcher matcher = pattern.matcher(contentBody.toLowerCase());
                            if (matcher.find()) {
                                hackLog(logger, GatewayConstant.getRealIp(originalRequest, ctx), FilterType.POST.name(), pattern.toString());
                                return true;
                            }
                        }
                        Matcher fileMatcher = filePattern.matcher(contentBody);
                        if (fileMatcher.find()) {
                            String fileExt = fileMatcher.group(3);
                            for (Pattern pat : ConfUtil.getPattern(FilterType.FILE.name())) {
                                if (pat.matcher(fileExt).matches()) {
                                    hackLog(logger, GatewayConstant.getRealIp(originalRequest, ctx), FilterType.POST.name(), filePattern.toString());
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
