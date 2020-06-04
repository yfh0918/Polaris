package com.polaris.core.exception;

public class ServletContextException extends PolarisRuntimeException{
    private static final long serialVersionUID = 1L;


    public static final int ERROR_CODE = 202;


    private static final String DEFAULT_MSG = "ServletContextLoad failed. ";

    private static final String MSG_FOR_SPECIFIED_CLASS = "ServletContextLoad for class [%s] failed. ";

    private Class<?> targetClass;

    public ServletContextException() {
        super(ERROR_CODE);
    }
    public ServletContextException(String message) {
        super(ERROR_CODE, message);
    }
    public ServletContextException(Class<?> targetClass) {
        super(ERROR_CODE, String.format(MSG_FOR_SPECIFIED_CLASS, targetClass.getName()));
        this.targetClass = targetClass;
    }

    public ServletContextException(Throwable throwable) {
        super(ERROR_CODE, DEFAULT_MSG, throwable);
    }

    public ServletContextException(Class<?> targetClass, Throwable throwable) {
        super(ERROR_CODE, String.format(MSG_FOR_SPECIFIED_CLASS, targetClass.getName()), throwable);
        this.targetClass = targetClass;
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }
}
