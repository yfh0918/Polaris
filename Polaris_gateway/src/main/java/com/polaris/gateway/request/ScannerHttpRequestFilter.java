package com.polaris.gateway.request;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.polaris.gateway.GatewayConstant;
import com.polaris.core.util.LogUtil;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;

/**
 * @author:Tom.Yu
 *
 * Description:
 *
 */
@Service
public class ScannerHttpRequestFilter extends HttpRequestFilter {
	private static LogUtil logger = LogUtil.getInstance(ScannerHttpRequestFilter.class);

    @Override
    public boolean doFilter(HttpRequest originalRequest, HttpObject httpObject, ChannelHandlerContext channelHandlerContext) {
        if (httpObject instanceof HttpRequest) {
            logger.debug("filter:{}", this.getClass().getName());
            HttpRequest httpRequest = (HttpRequest) httpObject;
            //Acunetix Web Vulnerability
            boolean acunetixAspect = httpRequest.headers().contains("Acunetix-Aspect");
            boolean acunetixAspectPassword = httpRequest.headers().contains("Acunetix-Aspect-Password");
            boolean acunetixAspectQueries = httpRequest.headers().contains("Acunetix-Aspect-Queries");

            //HP WebInspect
            boolean xScanMemo = httpRequest.headers().contains("X-Scan-Memo");
            boolean xRequestMemo = httpRequest.headers().contains("X-Request-Memo");
            boolean xRequestManagerMemo = httpRequest.headers().contains("X-RequestManager-Memo");
            boolean xWIPP = httpRequest.headers().contains("X-WIPP");

            //Appscan
            Pattern pattern1 = Pattern.compile("AppScan_fingerprint");
            Matcher matcher1 = pattern1.matcher(httpRequest.uri());

            //Bugscan
            String bsKey="--%3E%27%22%3E%3CH1%3EXSS%40HERE%3C%2FH1%3E";
            boolean matcher2 = httpRequest.uri().contains(bsKey);

            //Netsparker
            Pattern pattern3 = Pattern.compile("netsparker=");
            Matcher matcher3 = pattern3.matcher(httpRequest.uri());

            if (acunetixAspect || acunetixAspectPassword || acunetixAspectQueries) {
                hackLog(logger, GatewayConstant.getRealIp(httpRequest), "scanner", "Acunetix Web Vulnerability");
                return true;
            } else if (xScanMemo || xRequestMemo || xRequestManagerMemo || xWIPP) {
                hackLog(logger, GatewayConstant.getRealIp(httpRequest), "scanner", "HP WebInspect");
                return true;
            } else if (matcher1.find()) {
                hackLog(logger, GatewayConstant.getRealIp(httpRequest), "scanner", "Appscan");
                return true;
            } else if (matcher2) {
                hackLog(logger, GatewayConstant.getRealIp(httpRequest), "scanner", "Bugscan");
                return true;
            } else if (matcher3.find()) {
                hackLog(logger, GatewayConstant.getRealIp(httpRequest), "scanner", "Netsparker");
                return true;
            }
        }
        return false;
    }
}

