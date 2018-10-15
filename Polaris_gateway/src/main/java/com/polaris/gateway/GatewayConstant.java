package com.polaris.gateway;

import java.net.InetAddress;
import java.net.UnknownHostException;
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
    public static int AcceptorThreads = Integer.parseInt(ConfClient.get("server.acceptorThreads"));
    public static int ClientToProxyWorkerThreads = Integer.parseInt(ConfClient.get("server.clientToProxyWorkerThreads"));
    public static int ProxyToServerWorkerThreads = Integer.parseInt(ConfClient.get("server.proxyToServerWorkerThreads"));
    public static final String SERVER_PORT = ConfClient.get("server.port");
    public static X_Frame_Options X_Frame_Option = X_Frame_Options.SAMEORIGIN;
    public static final String X_Forwarded_For = "X-Forwarded-For";
    public static final String X_Real_IP = "X-Real-IP";
    public static final String OFF = "off";
    
    public static final String HOST = "Host";
    public static final String DEFAULT="default";

    public static String getRealIp(HttpRequest httpRequest, ChannelHandlerContext channelHandlerContext) {
        List<String> headerValues = getHeaderValues(httpRequest, X_Real_IP);
        String ip = headerValues.get(0);
        if ("127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip)) {
            try {
                ip = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException unknownhostexception) {
            }
        }
        return ip;
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
