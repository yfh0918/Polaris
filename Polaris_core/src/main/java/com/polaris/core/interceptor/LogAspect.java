package com.polaris.core.interceptor;

import org.aspectj.lang.ProceedingJoinPoint;

import com.polaris.comm.util.LogUtil;

public class LogAspect {
    private static final LogUtil logger = LogUtil.getInstance(LogAspect.class);
    private final String START = " START!!!";
    private final String END = " END!!!";
    private final String ERROR = " ERROR";
    
    /*
     * 执行service
     */
    public Object executeService(ProceedingJoinPoint joinPoint) throws Throwable {

        logger.info(START);
        Object result = joinPoint.proceed();
        logger.info(END);
        return result;
    }

    /*
     * 执行Mapper
     */
    public Object executeMapper(ProceedingJoinPoint joinPoint) throws Throwable {
        logger.info(START);
        Object result = joinPoint.proceed();
        logger.info(END);
        return result;
    }

    //打印错误信息
    protected void writeErrorLog(ProceedingJoinPoint joinPoint, Exception e) throws Throwable {
        logger.error(ERROR, e);
    }
}
