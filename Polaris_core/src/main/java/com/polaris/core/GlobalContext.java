package com.polaris.core;

import java.util.HashMap;
import java.util.Map;

import com.polaris.core.config.ConfClient;
import com.polaris.core.thread.InheritablePolarisThreadLocal;

public class GlobalContext {
	public static final String REQUEST = "request";
	public static final String RESPONSE = "response";
	public static final String TRACE_ID = "traceId";
	public static final String PARENT_ID = "parentId";
	public static final String SPAN_ID = "spanId";
	public static final String MODULE_ID = "moduleId";// 本模块ID
	public static String getTraceId() {
		Object objTraceId = GlobalContext.getContext(GlobalContext.TRACE_ID);
		if (objTraceId == null) {
			return null;
		}
		return objTraceId.toString();
	}

	public static void setTraceId(String traceId) {
		GlobalContext.setContext(GlobalContext.TRACE_ID, traceId);
	}
	
	public static String getSpanId() {
		Object objSpanId = GlobalContext.getContext(GlobalContext.SPAN_ID);
		if (objSpanId == null) {
			return null;
		}
		return objSpanId.toString();
	}
	public static void setSpanId(String spanId) {
		GlobalContext.setContext(GlobalContext.SPAN_ID, spanId);
	}
	
	public static String getParentId() {
		Object objParentId = GlobalContext.getContext(GlobalContext.PARENT_ID);
		if (objParentId == null) {
			return null;
		}
		return objParentId.toString();
	}

	public static void setParentId(String parentId) {
		GlobalContext.setContext(GlobalContext.PARENT_ID, parentId);
	}

	public static String getModuleId() {
		return  ConfClient.getAppName();
	}
	//构造函数
	private static final InheritablePolarisThreadLocal<Map<String, Object>> holder=new InheritablePolarisThreadLocal<Map<String,Object>>(){
		@Override protected Map<String,Object>initialValue(){
			return new HashMap<String,Object>();
		}
	};

	public static void setContext(String key, Object value) {
		Map<String, Object> map = holder.get();
		map.put(key, value);
		holder.set(map);
	}
	public static Object getContext(String key) {
		return holder.get().get(key);
	}
	public static void removeContext(String key) {
		holder.get().remove(key);
	}
	public static Map<String,Object> getContext() {
		return holder.get();
	}
	public static void removeContext() {
		holder.remove();
	}
}
