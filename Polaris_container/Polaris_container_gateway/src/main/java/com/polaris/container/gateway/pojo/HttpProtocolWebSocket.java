package com.polaris.container.gateway.pojo;

import com.polaris.core.util.StringUtil;

public abstract class HttpProtocolWebSocket {
    
    private static boolean INITIAL = true;
    
    private static int WS_REQUEST_MAX_NMBER = 2000;
    
    private static int WS_IDLE_CONNECT_TIMEOUT = 600;
    
    private static int WS_MAX_FRAME_PAYLOAD_LENGTH = 65536;

    
    public static int getRequestMaxNumber() {
        init();
        return WS_REQUEST_MAX_NMBER;
    }
    public static int getIdleConnectTimeout() {
        init();
        return WS_IDLE_CONNECT_TIMEOUT;
    }
    public static int getMaxFramePayloadLength() {
        init();
        return WS_MAX_FRAME_PAYLOAD_LENGTH;
    }
    
    private static void init() {
        if (INITIAL) {
            String requestMaxNumber = HttpProtocol.getWebsocketMap().get("requestMaxNumber");
            if (StringUtil.isNotEmpty(requestMaxNumber)) {
                try {
                    WS_REQUEST_MAX_NMBER = Integer.parseInt(requestMaxNumber);
                } catch (Exception ex) {}
            }
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
            INITIAL = false;
        }
    }
}
