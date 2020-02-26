package com.polaris.demo.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.polaris.core.datasource.DynamicDataSourceAspect;

@Component
@Aspect
public class DataBaseAop extends DynamicDataSourceAspect{
	
	@Around(  "execution(* com.polaris.demo.*.*(..))")
    public Object proceed(ProceedingJoinPoint point) throws Throwable {
		//处理正常业务逻辑
		before(point);
        return point.proceed();
    }
    

}
