package com.polaris.container.gateway.pojo;

import com.polaris.core.util.StringUtil;

public abstract class HttpProtocolWebSocket {
    
    private static int WS_IDLE_CONNECT_TIMEOUT = 600;
    
    private static int WS_MAX_FRAME_PAYLOAD_LENGTH = 65536;
    static {
        init();
    }
    public static int getIdleConnectTimeout() {
        return WS_IDLE_CONNECT_TIMEOUT;
    }
    public static int getMaxFramePayloadLength() {
        return WS_MAX_FRAME_PAYLOAD_LENGTH;
    }
    
    private static void init() {
        String idleTimeout = HttpProtocol.getWebsocketMap().get("idleTimeout");
        if (StringUtil.isNotEmpty(idleTimeout)) {
            try {
                WS_IDLE_CONNECT_TIMEOUT = Integer.parseInt(idleTimeout);
            } catch (Exception ex) {}
        }
        String maxFramePayloadLength = HttpProtocol.getWebsocketMap().get("maxFramePayloadLength");
        if (StringUtil.isNotEmpty(maxFramePayloadLength)) {
            try {
                WS_MAX_FRAME_PAYLOAD_LENGTH = Integer.parseInt(maxFramePayloadLength);
            } catch (Exception ex) {}
        }
    }
}
