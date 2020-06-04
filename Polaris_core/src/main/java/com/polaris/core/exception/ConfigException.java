package com.polaris.core.exception;

public class ConfigException extends PolarisRuntimeException{

    private static final long serialVersionUID = 1L;


    public static final int ERROR_CODE = 201;


    private static final String DEFAULT_MSG = "config failed. ";

    private static final String MSG_FOR_SPECIFIED_CLASS = "config for class [%s] failed. ";

    private Class<?> targetClass;

    public ConfigException() {
        super(ERROR_CODE);
    }
    public ConfigException(String message) {
        super(ERROR_CODE, message);
    }
    public ConfigException(Class<?> targetClass) {
        super(ERROR_CODE, String.format(MSG_FOR_SPECIFIED_CLASS, targetClass.getName()));
        this.targetClass = targetClass;
    }

    public ConfigException(Throwable throwable) {
        super(ERROR_CODE, DEFAULT_MSG, throwable);
    }

    public ConfigException(Class<?> targetClass, Throwable throwable) {
        super(ERROR_CODE, String.format(MSG_FOR_SPECIFIED_CLASS, targetClass.getName()), throwable);
        this.targetClass = targetClass;
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }
}
