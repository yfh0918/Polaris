package com.polaris.core.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
        Map<String, T> beanMap = SpringContextHealper.getApplicationContext().getBeansOfType(cls);
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
    
    public static Set<Class<?>> getAllSuperClasses(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }
        Set<Class<?>> classes = new LinkedHashSet<>();
        Class<?> superclass = clazz.getSuperclass();
        while (superclass != null && superclass != Object.class) {
            classes.add(superclass);
            superclass = superclass.getSuperclass();
        }
        return classes;
    }
    
    public static Set<Type> getAllSuperTypes(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }
        Set<Type> types = new LinkedHashSet<>();
        types.add(clazz.getGenericSuperclass());
        Class<?> superclass = clazz.getSuperclass();
        while (superclass != null && superclass != Object.class) {
            types.add(superclass.getGenericSuperclass());
            superclass = superclass.getSuperclass();
        }
        return types;
    }
    
}
