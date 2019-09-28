package com.polaris.gateway;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.polaris.core.config.ConfClient;

import io.netty.handler.codec.http.HttpMessage;

/**
 * @author:Tom.Yu
 *
 * Description:
 *
 */
public class GatewayConstant {
    enum X_Frame_Options {
        DENY,//表示该页面不允许在 frame 中展示,即便是在相同域名的页面中嵌套也不允许.
        SAMEORIGIN//表示该页面可以在相同域名页面的 frame 中展示.
    }
    public static int AcceptorThreads = Integer.parseInt(ConfClient.get("server.acceptorThreads"));
    public static int ClientToProxyWorkerThreads = Integer.parseInt(ConfClient.get("server.clientToProxyWorkerThreads"));
    public static int ProxyToServerWorkerThreads = Integer.parseInt(ConfClient.get("server.proxyToServerWorkerThreads"));
    
    /**
     * RFC7230/RFC7231/RFC7232/RFC7233/RFC7234
     * Each header field consists of a case-insensitive field name followed
     * by a colon (":"), optional leading whitespace, the field value, and
     * optional trailing whitespace.
     *
     * @param httpMessage
     * @param headerName
     * @return headerValue
     */
    public static List<String> getHeaderValues(HttpMessage httpMessage, String headerName) {
        List<String> list = Lists.newArrayList();
        for (Map.Entry<String, String> header : httpMessage.headers().entries()) {
            if (header.getKey().toLowerCase().equals(headerName.toLowerCase())) {
                list.add(header.getValue());
            }
        }
        return list;
    }
    
}
