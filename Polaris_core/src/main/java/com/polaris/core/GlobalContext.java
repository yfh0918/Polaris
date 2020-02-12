package com.polaris.core;

import java.util.HashMap;
import java.util.Map;

import com.polaris.core.config.ConfClient;
import com.polaris.core.config.ConfHandlerEnum;
import com.polaris.core.thread.InheritablePolarisThreadLocal;

public class GlobalContext {
	public static final String REQUEST = "request";
	public static final String RESPONSE = "response";
	public static final String TRACE_ID = "traceId";
	public static final String PARENT_ID = "parentId";
	public static final String MODULE_ID = "moduleId";// 本模块ID
	public static String getTraceId() {
		if (GlobalContext.getContext(GlobalContext.TRACE_ID) == null) {
			return null;
		}
		return GlobalContext.getContext(GlobalContext.TRACE_ID).toString();
	}

	public static void setTraceId(String traceId) {
		GlobalContext.setContext(GlobalContext.TRACE_ID, traceId);
	}
	
	public static String getParentId() {
		if (GlobalContext.getContext(GlobalContext.PARENT_ID) == null) {
			return null;
		}
		return GlobalContext.getContext(GlobalContext.PARENT_ID).toString();
	}

	public static void setParentId(String parentId) {
		GlobalContext.setContext(GlobalContext.PARENT_ID, parentId);
	}

	public static String getModuleId() {
		return  ConfClient.getAppName() + "|" +	
				ConfHandlerEnum.DEFAULT.get(Constant.IP_ADDRESS) + "|" +
				ConfHandlerEnum.DEFAULT.get(Constant.SERVER_PORT_NAME);
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
