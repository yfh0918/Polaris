package com.polaris.core.pojo;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class Parameter implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 请求参数Map
	 */
	private Map<String, Object> parameterMap = new LinkedHashMap<>();
	/**
	 * 获取map
	 */
	public Map<String, Object> getParameterMap() {
		return parameterMap;
	}
	
	public void setParameterMap(Map<String, Object> parameterMap) {
		this.parameterMap = parameterMap;
	}
}
