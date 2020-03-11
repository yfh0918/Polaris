package com.polaris.core.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

import org.springframework.core.convert.support.DefaultConversionService;

public abstract class ReflectionUtil {
	private static String METHOD_PREFIX_SET = "set";
	private static DefaultConversionService conversionService = new DefaultConversionService();
    
    public static String getFieldNameForSet(Method method) {
    	if (!method.getName().startsWith(METHOD_PREFIX_SET)) {
    		return null;
    	}
    	String fieldName = method.getName().substring(METHOD_PREFIX_SET.length());
    	return fieldName.substring(0,1).toLowerCase().concat(fieldName.substring(1));
    }
    
    public static void setMethodValue(Method method,Object obj,Object fieldValue) throws RuntimeException{
    	try {
    		if (!method.isAccessible()) {
        		method.setAccessible(true);
        	}
        	
        	Class<?>[] parameterTypes = method.getParameterTypes();
    		if (parameterTypes == null || parameterTypes.length != 1) {
    			return;
    		}
    		method.invoke(obj, fieldValue == null ? null : conversionService.convert(fieldValue, parameterTypes[0]));
    	} catch (Exception ex) {
    		throw new RuntimeException(ex);
    	}
    	
    }
    
    @SuppressWarnings("unchecked")
	public static Map<String, Object> getMemberValuesMap(Class<?> clazz, Class<? extends Annotation> annotationType) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
    	Annotation annotation = clazz.getAnnotation(annotationType);
		InvocationHandler invocationHandler = Proxy.getInvocationHandler(annotation);
		Field value = invocationHandler.getClass().getDeclaredField("memberValues");
		value.setAccessible(true);
		Map<String, Object> memberValuesMap = (Map<String, Object>) value.get(invocationHandler);
		return memberValuesMap;
    }
    

}
