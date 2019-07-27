package com.polaris.http.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;

import com.polaris.core.dto.ParameterDto;
import com.polaris.core.dto.ResultDto;
import com.polaris.core.util.LogUtil;
import com.polaris.core.util.StringUtil;

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
	        if (StringUtil.isNotEmpty(value)) {
	        	if (parameterMap.get(key) != null) {
		        	parameterMap.put(key, parameterMap.get(key).toString() + "|polaris|" + value);//自定义分割协议
		        } else {
		        	parameterMap.put(key, value);
		        }
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
	
	public static String getRequestBody(HttpServletRequest request) {
		String requestBody = "";
		StringBuilder stringBuilder = new StringBuilder();
		BufferedReader bufferedReader = null;
		try {
		    InputStream inputStream = request.getInputStream();
		    if (inputStream != null) {
		        bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
		        char[] charBuffer = new char[128];
		        int bytesRead = -1;
		        while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
		            stringBuilder.append(charBuffer, 0, bytesRead);
		        }
		    } else {
		        stringBuilder.append("");
		    }
		} catch (IOException ex) {
			return requestBody;
		} finally {
		    if (bufferedReader != null) {
		        try {
		            bufferedReader.close();
		        } catch (IOException ex) {
		        	return requestBody;
		        }
		    }
		}
		requestBody = stringBuilder.toString();
		return requestBody;
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
	    if (rtnObject instanceof ParameterDto) {
	    	((ParameterDto)rtnObject).setParameterMap(parameterMap);
	    }
	    
	    if (rtnObject instanceof ResultDto) {
	    	((ResultDto)rtnObject).setParameterMap(parameterMap);
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
