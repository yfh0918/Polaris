package com.polaris.container.gateway.proxy.websocket;

import com.polaris.core.exception.PolarisRuntimeException;

public class WebSocketException extends PolarisRuntimeException{
    private static final long serialVersionUID = 1L;

    public static final int ERROR_CODE = 300;

    private static final String DEFAULT_MSG = "websocket failed. ";

    public WebSocketException() {
        super(ERROR_CODE);
    }

    public WebSocketException(Throwable throwable) {
        super(ERROR_CODE, DEFAULT_MSG, throwable);
    }
}
