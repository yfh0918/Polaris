package com.polaris.core.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.core.util.MapUtil;
import com.polaris.core.util.SpringUtil;

/**
 * service管理类，用于实现针对不同的系统做不同处理
 */
public class ServiceAdapter {
	
	private static final Logger logger = LoggerFactory.getLogger(ServiceAdapter.class);

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
    
}
