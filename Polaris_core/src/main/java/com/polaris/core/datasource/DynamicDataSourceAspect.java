package com.polaris.core.datasource;

import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DynamicDataSourceAspect {

    private static final Logger logger =  LoggerFactory.getLogger(DynamicDataSourceAspect.class);

    public void before(JoinPoint point) {
        Object target = point.getTarget();
        String method = point.getSignature().getName();

        Class<?>[] classz = target.getClass().getInterfaces();
        Class<?>[] parameterTypes = ((MethodSignature) point.getSignature()).getMethod().getParameterTypes();
        try {
        	for (Class<?> clazz : classz) {
        		Method m = clazz.getMethod(method, parameterTypes);
                if (m != null && m.isAnnotationPresent(DataSource.class)) {
                    DataSource data = m.getAnnotation(DataSource.class);
                    DynamicDataSourceHolder.setDataSource(data.value());
                    break;
                }
        	}
            
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
