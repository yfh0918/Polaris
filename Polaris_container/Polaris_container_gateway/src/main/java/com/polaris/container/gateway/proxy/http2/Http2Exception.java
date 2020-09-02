package com.polaris.container.gateway.proxy.http2;

import com.polaris.core.exception.PolarisRuntimeException;

public class Http2Exception extends PolarisRuntimeException{
    private static final long serialVersionUID = 1L;

    public static final int ERROR_CODE = 302;

    private static final String DEFAULT_MSG = "http2 failed. ";

    public Http2Exception() {
        super(ERROR_CODE);
    }

    public Http2Exception(Throwable throwable) {
        super(ERROR_CODE, DEFAULT_MSG, throwable);
    }

    public Http2Exception(String message, Throwable throwable) {
        super(ERROR_CODE, message, throwable);
    }
}
