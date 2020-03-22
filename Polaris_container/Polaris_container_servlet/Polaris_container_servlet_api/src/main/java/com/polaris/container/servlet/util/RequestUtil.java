package com.polaris.container.servlet.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.polaris.core.pojo.Parameter;
import com.polaris.core.util.StringUtil;

public final class RequestUtil {

	private static final Logger logger =  LoggerFactory.getLogger(RequestUtil.class);

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
		logger.debug("requestMap:{}",parameterMap);
	    return parameterMap;
	}
	
	public static <T> T convertParameterToObject(HttpServletRequest request, Class<T> clazz) {
		return convertParameterToObject(request, clazz, new Feature[0]);
	}
	public static <T> T convertParameterToObject(HttpServletRequest request, Class<T> clazz, Feature... feature) {
		
		//获取转换的对象Map
		Map<String, Object> parameterMap = convertParameterToMap(request);
		
		//系统认定的
		T rtnObject = JSONObject.parseObject(JSONObject.toJSONString(parameterMap), clazz, feature);
	    if (rtnObject instanceof Parameter) {
	    	((Parameter)rtnObject).setParameterMap(parameterMap);
	    } 
    	return rtnObject;
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
		logger.debug("requestBody:{}",requestBody);
		return requestBody;
	}
	
	public static <T> T getRequestBodyToObject(HttpServletRequest request, Class<T> clazz) {
		return getRequestBodyToObject(request, clazz, new Feature[0]);
	}
	
	public static <T> T getRequestBodyToObject(HttpServletRequest request, Class<T> clazz, Feature... feature) {
		String body = getRequestBody(request);
		return JSONObject.parseObject(body, clazz, feature);
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
