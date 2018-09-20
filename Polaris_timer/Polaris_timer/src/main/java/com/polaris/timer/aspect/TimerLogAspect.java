package com.polaris.timer.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import com.polaris.core.interceptor.LogAspect;
import com.polaris.timer.api.exception.TimerException;

@Component
@Aspect
public class TimerLogAspect extends LogAspect implements Ordered {

    /*
     * 通过连接点切入
     */
    @Around("execution(* com.polaris.timer.service.*.*(..))")
    public Object executeService(ProceedingJoinPoint joinPoint) throws Throwable {

        //日志开启
        try {
            return super.executeService(joinPoint);
        } catch (Exception e) {
            if (e instanceof TimerException) {
                throw e;
            } else {
                writeErrorLog(joinPoint, e);
                throw new TimerException(e);
            }
        }
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
