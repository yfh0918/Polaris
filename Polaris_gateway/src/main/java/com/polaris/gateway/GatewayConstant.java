package com.polaris.gateway;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.polaris.comm.config.ConfClient;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpRequest;

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
    public static final String config = "config";
    public static final String log4j = "log4j.properties";
    public static int AcceptorThreads = Integer.parseInt(ConfClient.get("gateway.acceptorThreads"));
    public static int ClientToProxyWorkerThreads = Integer.parseInt(ConfClient.get("gateway.clientToProxyWorkerThreads"));
    public static int ProxyToServerWorkerThreads = Integer.parseInt(ConfClient.get("gateway.proxyToServerWorkerThreads"));
    public static final String SERVER_PORT = ConfClient.get("gateway.serverPort");
    public static X_Frame_Options X_Frame_Option = X_Frame_Options.SAMEORIGIN;
    public static final String X_Forwarded_For = "X-Forwarded-For";
    public static final String X_Real_IP = "X-Real-IP";
    public static final String OFF = "off";
    public static final String GATEWAY_CACHE_NAME = "Polaris_gateway";
    
    public static final String HOST = "Host";
    public static final String PORT = "PORT_";
    public static final String DEFAULT="default";

    public static String getRealIp(HttpRequest httpRequest, ChannelHandlerContext channelHandlerContext) {
        List<String> headerValues = getHeaderValues(httpRequest, X_Real_IP);
        return headerValues.get(0);
    }

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
