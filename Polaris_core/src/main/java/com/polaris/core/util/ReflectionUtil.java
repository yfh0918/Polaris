package com.polaris.core.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.core.config.ConfClient;

public abstract class ReflectionUtil {
	private static Logger logger = LoggerFactory.getLogger(ReflectionUtil.class);
	private static String METHOD_PREFIX_SET = "set";

    public static void setMethodValueForSet(Method method, Object obj, String configPrefix) {
    	if (!method.getName().startsWith(METHOD_PREFIX_SET)) {
    		return;
    	}
    	String fieldName = method.getName().substring(METHOD_PREFIX_SET.length());
    	fieldName = configPrefix + fieldName.substring(0,1).toLowerCase().concat(fieldName.substring(1));
    	
    	String fieldValue = ConfClient.get(fieldName);
		if (StringUtil.isEmpty(fieldValue)) {
			return;
		}
    	if (!method.isAccessible()) {
    		method.setAccessible(true);
    	}
		try {
			Class<?>[] parameterTypes = method.getParameterTypes();
			if (parameterTypes == null || parameterTypes.length != 1) {
				return;
			}
	    	if (parameterTypes[0] == String.class) {
	    		method.invoke(obj, fieldValue);
	    	} else if (parameterTypes[0] == Integer.class) {
	    		method.invoke(obj, Integer.parseInt(fieldValue));
	    	} else if (parameterTypes[0] == Boolean.class) {
	    		method.invoke(obj, Boolean.parseBoolean(fieldValue));
	    	} else if (parameterTypes[0] == Long.class) {
	    		method.invoke(obj, Long.parseLong(fieldValue));
	    	}
		} catch (Exception e) {
			logger.error("ERROR:",e);
		} 
    }
    
    public static void setFieldValue(Field field, Object obj, String fieldName, String configPrefix) {
    	String fieldValue = ConfClient.get(configPrefix + fieldName);
		if (StringUtil.isEmpty(fieldValue)) {
			return;
		}
		try {
	    	if (field.getType() == String.class) {
	    		field.setAccessible(true);
				field.set(obj, fieldValue);
	    	} else if (field.getType() == Integer.class) {
	    		field.setAccessible(true);
				field.set(obj, Integer.parseInt(fieldValue));
	    	} else if (field.getType() == Boolean.class) {
	    		field.setAccessible(true);
				field.set(obj, Boolean.parseBoolean(fieldValue));
	    	} else if (field.getType() == Long.class) {
	    		field.setAccessible(true);
				field.set(obj, Long.parseLong(fieldValue));
	    	}
		} catch (Exception e) {
			logger.error("ERROR:",e);
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
