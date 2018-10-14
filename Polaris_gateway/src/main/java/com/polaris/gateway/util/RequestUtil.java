package com.polaris.gateway.util;

import java.util.HashMap;
import java.util.Map;

import com.polaris.comm.thread.InheritablePolarisThreadLocal;

public class RequestUtil {

	// query string
    private static final String QUERY_STRING = "queryString";
    private static final String POST_PARAMETER = "postParameter";
    private static final String COOKIE_PARAMETER = "cookieParameter";
	private static final InheritablePolarisThreadLocal<Map<String, Map<String, String>>> holder=new InheritablePolarisThreadLocal<Map<String,Map<String, String>>>(){
		@Override protected Map<String,Map<String, String>>initialValue(){
			Map<String, Map<String, String>> map = new HashMap<>();
			map.put(QUERY_STRING, new HashMap<>());
			map.put(POST_PARAMETER, new HashMap<>());
			map.put(COOKIE_PARAMETER, new HashMap<>());
			return map;
		}
	};

	public static String getQueryString(String key) {
		return holder.get().get(QUERY_STRING).get(key);
	}
	public static void setQueryString(String key, String value) {
		holder.get().get(QUERY_STRING).put(key, value);
	}
	public static String getPostParameter(String key) {
		return holder.get().get(POST_PARAMETER).get(key);
	}
	public static void setPostParameter(String key, String value) {
		holder.get().get(POST_PARAMETER).put(key, value);
	}
	public static String getRequestParameter(String key) {
		return getQueryString(key) == null ? getPostParameter(key) : getQueryString(key);
	}
	public static String getCookie(String key) {
		return holder.get().get(COOKIE_PARAMETER).get(key);
	}
	public static void setCookie(String key, String value) {
		holder.get().get(COOKIE_PARAMETER).put(key, value);
	}
	public static void remove() {
		holder.remove();
	}
}
