package com.polaris.http.util;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;

import com.polaris.comm.dto.BaseDto;
import com.polaris.comm.util.LogUtil;
import com.polaris.comm.util.StringUtil;

public final class RequestUtil {

	private static final LogUtil logger =  LogUtil.getInstance(RequestUtil.class);

	@SuppressWarnings("rawtypes")
	public static Map<String, Object> convertParameterToMap(HttpServletRequest request) {
		
		Map<String, Object> parameterMap = new LinkedHashMap<>();
		//获取所有的request参数
		Map requestMap = request.getParameterMap();  
	    Set keSet = requestMap.entrySet(); 
	    for(Iterator itr=keSet.iterator();itr.hasNext();){  
	        Map.Entry me=(Map.Entry)itr.next();  
	        String key = me.getKey().toString();  
	        Object ov =me.getValue(); 
	        if (key == null || ov == null||StringUtil.isEmptyOfStrict(ov.toString())) {
	        	continue;
	        }
	        if (key.indexOf('[') >= 0) {
	        	key = key.substring(0, key.indexOf('['));
	        }
	        String value;  
	        if(ov instanceof String[]){  
	            value=((String[])ov)[0];  
	        }else{  
	            value=ov.toString();  
	        }
	        if (parameterMap.get(key) != null) {
	        	parameterMap.put(key, parameterMap.get(key).toString() + "|polaris|" + value);//自定义分割协议
	        } else {
	        	parameterMap.put(key, value);
	        }
	    } 
	    
	    //载入map
	    for (Map.Entry<String, Object> entry : parameterMap.entrySet()) {
	    	String[] valueList = entry.getValue().toString().split("\\|polaris\\|");//自定义分割协议
	    	if (valueList.length == 1) {
	    		parameterMap.put(entry.getKey(), valueList[0]);
	    	} else {
	    		parameterMap.put(entry.getKey(), valueList);
	    	}
	    }
	    return parameterMap;
	}
	
	@SuppressWarnings("rawtypes")
	public static <T> T convertParameterToObject(HttpServletRequest request, Class<T> clazz) {
		
		Map<String, Object> parameterMap = new LinkedHashMap<>();
		T rtnObject;
		try {
			rtnObject = clazz.newInstance();
		} catch (InstantiationException e) {
			logger.error(e);
			return null;
		} catch (IllegalAccessException e) {
			logger.error(e);
			return null;
		}
		//获取所有的request参数
		Map requestMap = request.getParameterMap();  
	    Set keSet = requestMap.entrySet(); 
	    for(Iterator itr=keSet.iterator();itr.hasNext();){  
	        Map.Entry me=(Map.Entry)itr.next();  
	        String key = me.getKey().toString();  
	        Object ov =me.getValue(); 
	        if (key == null || ov == null||StringUtil.isEmptyOfStrict(ov.toString())) {
	        	continue;
	        }
	        if (key.indexOf('[') >= 0) {
	        	key = key.substring(0, key.indexOf('['));
	        }
	        String value;  
	        if(ov instanceof String[]){  
	            value=((String[])ov)[0];  
	        }else{  
	            value=ov.toString();  
	        }
	        if (parameterMap.get(key) != null) {
	        	parameterMap.put(key, parameterMap.get(key).toString() + "|polaris|" + value);//自定义分割协议
	        } else {
	        	parameterMap.put(key, value);
	        }
	    } 
	    
	    //载入map
	    for (Map.Entry<String, Object> entry : parameterMap.entrySet()) {
	    	String[] valueList = entry.getValue().toString().split("\\|polaris\\|");//自定义分割协议
	    	if (valueList.length == 1) {
	    		parameterMap.put(entry.getKey(), valueList[0]);
	    		try {
        			BeanUtils.setProperty(rtnObject, entry.getKey(), valueList[0]);
	    		} catch (Exception ex) {
					//nothing
	        	}
	    	} else {
	    		parameterMap.put(entry.getKey(), valueList);
	    		try {
        			BeanUtils.setProperty(rtnObject, entry.getKey(), valueList);
	    		} catch (Exception ex) {
					//nothing
	        	}
	    	}
	    }
	    if (rtnObject instanceof BaseDto) {
	    	((BaseDto)rtnObject).setParameterMap(parameterMap);
	    }
	    return rtnObject;
	}
	
	
    /**
     * 判断一个请求是ajax
     *
     * @return
     */
    public static boolean isAjaxRequest(HttpServletRequest request) {
        String header = request.getHeader("X-Requested-With");
        if ("XMLHttpRequest".equals(header)) {
            return true;
        } else {
            return false;
        }
    }
    
	//构造函数
	private RequestUtil () {
	}
	

}
