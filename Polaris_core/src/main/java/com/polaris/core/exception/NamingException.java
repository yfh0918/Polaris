package com.polaris.core.exception;

public class NamingException extends PolarisRuntimeException{

    private static final long serialVersionUID = 1L;

    public static final int ERROR_CODE = 701;

    private static final String DEFAULT_MSG = "naming failed. ";

    private static final String MSG_FOR_SPECIFIED_CLASS = "naming for class [%s] failed. ";

    private Class<?> targetClass;

    public NamingException() {
        super(ERROR_CODE);
    }
    public NamingException(String message) {
        super(ERROR_CODE, message);
    }
    public NamingException(Class<?> targetClass) {
        super(ERROR_CODE, String.format(MSG_FOR_SPECIFIED_CLASS, targetClass.getName()));
        this.targetClass = targetClass;
    }

    public NamingException(Throwable throwable) {
        super(ERROR_CODE, DEFAULT_MSG, throwable);
    }

    public NamingException(Class<?> targetClass, Throwable throwable) {
        super(ERROR_CODE, String.format(MSG_FOR_SPECIFIED_CLASS, targetClass.getName()), throwable);
        this.targetClass = targetClass;
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }
}
