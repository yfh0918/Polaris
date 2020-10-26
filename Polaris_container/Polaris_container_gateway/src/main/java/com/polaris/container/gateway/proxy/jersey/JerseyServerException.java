package com.polaris.container.gateway.proxy.jersey;

import com.polaris.core.exception.PolarisRuntimeException;

public class JerseyServerException extends PolarisRuntimeException{
    private static final long serialVersionUID = 1L;

    public static final int ERROR_CODE = 601;

    private static final String DEFAULT_MSG = "JerseyServer failed. ";

    private static final String MSG_FOR_SPECIFIED_CLASS = "JerseyServer for class [%s] failed. ";

    private Class<?> targetClass;

    public JerseyServerException() {
        super(ERROR_CODE);
    }
    public JerseyServerException(String message) {
        super(ERROR_CODE, message);
    }
    public JerseyServerException(Class<?> targetClass) {
        super(ERROR_CODE, String.format(MSG_FOR_SPECIFIED_CLASS, targetClass.getName()));
        this.targetClass = targetClass;
    }

    public JerseyServerException(Throwable throwable) {
        super(ERROR_CODE, DEFAULT_MSG, throwable);
    }

    public JerseyServerException(Class<?> targetClass, Throwable throwable) {
        super(ERROR_CODE, String.format(MSG_FOR_SPECIFIED_CLASS, targetClass.getName()), throwable);
        this.targetClass = targetClass;
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }
}
