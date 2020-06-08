package com.polaris.core.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.support.DefaultConversionService;

/**
 * Class关联处理
 */
@SuppressWarnings("unchecked")
public class ClassUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(ClassUtil.class);
    private static String METHOD_PREFIX_SET = "set";
    private static DefaultConversionService conversionService = new DefaultConversionService();

	//获取指定的service
    public static <T>T findServiceImpl(Class<T> cls) {
    	
    	//不存在直接返回空
        if (cls == null) {
            return null;
        }
        Map<String, T> beanMap = SpringUtil.getApplicationContext().getBeansOfType(cls);
        if (beanMap == null || beanMap.size() == 0) {
        	return null;
        }
        
        //只有一个实现类，直接返回
        if (beanMap.size() == 1) {
        	for (Map.Entry<String, T> entry : beanMap.entrySet()) {
            	return entry.getValue();
            }
        }
        
        //多个实现类，说明有继承关系，获取最终继承类，比如 c extends b, b extend a 则返回 c
        Map<T, Integer> classMap = new HashMap<>(); 
        for (Map.Entry<String, T> entry : beanMap.entrySet()) {
        	try {
            	String className = entry.getValue().toString();
            	
            	//代理对象
            	int index = className.indexOf('@');
            	if (index > 0) {
                	className = className.substring(0, index);
                	classMap.put(entry.getValue(), getSuperClassNumber(Class.forName(className)));
            	} else {
            		
            		//非代理对象
                	classMap.put(entry.getValue(), getSuperClassNumber(entry.getValue().getClass()));
            	}
        	} catch (Exception ex) {
        		logger.error(ex.getMessage());
        		return null;
        	}
        }
        LinkedHashMap<T, Integer> map = MapUtil.sortByValue(classMap);
        Entry<T, Integer> entry = MapUtil.getTail(map);
        return entry.getKey();
    }

    //查找所有父类的数量
    private static int getSuperClassNumber(Class<?> calzz){  
    	List<Class<?>> listSuperClass = new ArrayList<Class<?>>();  
        Class<?> superclass = calzz.getSuperclass();  
        while (superclass != null) {
            if("java.lang.Object".equals(superclass.getName())){  
                break;  
            }  
            listSuperClass.add(superclass); 
            superclass = superclass.getSuperclass();  
        }  
        return listSuperClass.size(); 
    }
    
	public static <T> T convert(Object obj, Class<T> clazz) {
		if (obj == null) {
			return null;
		}
		if (clazz.isAssignableFrom(obj.getClass())) {
			return (T)obj;
		}
		return null;
	}
    
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
    
    public static Map<String, Object> getMemberValuesMap(Class<?> clazz, Class<? extends Annotation> annotationType) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        Annotation annotation = clazz.getAnnotation(annotationType);
        InvocationHandler invocationHandler = Proxy.getInvocationHandler(annotation);
        Field value = invocationHandler.getClass().getDeclaredField("memberValues");
        value.setAccessible(true);
        Map<String, Object> memberValuesMap = (Map<String, Object>) value.get(invocationHandler);
        return memberValuesMap;
    }
    
    static class MapUtil {
    	
    	private static final Logger logger = LoggerFactory.getLogger(MapUtil.class);

    	//根据key排序
    	public static <K extends Comparable<? super K>, V > LinkedHashMap<K, V> sortByKey(Map<K, V> map) {
    		LinkedHashMap<K, V> result = new LinkedHashMap<>();
            Stream<Entry<K, V>> st = map.entrySet().stream();
            st.sorted(Comparator.comparing(e -> e.getKey())).forEach(e -> result.put(e.getKey(), e.getValue()));
            return result;
        }	
    		
    	//根据value排序
    	public static <K, V extends Comparable<? super V>> LinkedHashMap<K, V> sortByValue(Map<K, V> map) {
    		LinkedHashMap<K, V> result = new LinkedHashMap<>();
            Stream<Entry<K, V>> st = map.entrySet().stream();
            st.sorted(Comparator.comparing(e -> e.getValue())).forEach(e -> result.put(e.getKey(), e.getValue()));
            return result;
        }
    	
    	//获取第一个元素
    	public static <K, V> Entry<K, V> getHead(LinkedHashMap<K, V> map) {
    	    return map.entrySet().iterator().next();
    	}
    	
    	//获取最后一个元素
    	public static <K, V> Entry<K, V> getTail(LinkedHashMap<K, V> map) {
    		try {
    			Field tail = map.getClass().getDeclaredField("tail");
    		    tail.setAccessible(true);
    		    return (Entry<K, V>) tail.get(map);
    		} catch (Exception ex) {
    			logger.error(ex.getMessage());
    			return null;
    		}
    	}
    }
    
}
