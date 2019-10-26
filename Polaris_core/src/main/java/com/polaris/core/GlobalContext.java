package com.polaris.core;

import java.util.HashMap;
import java.util.Map;

import com.polaris.core.config.ConfClient;
import com.polaris.core.config.ConfigEnum;
import com.polaris.core.thread.InheritablePolarisThreadLocal;

public class GlobalContext {
	
	public static final String TRACE_ID = "traceId";
	public static final String PARENT_ID = "parentId";
	public static final String MODULE_ID = "moduleId";// 本模块ID
	public static String getTraceId() {
		return GlobalContext.getContext(GlobalContext.TRACE_ID);
	}

	public static void setTraceId(String traceId) {
		GlobalContext.setContext(GlobalContext.TRACE_ID, traceId);
	}
	
	public static String getParentId() {
		return GlobalContext.getContext(GlobalContext.PARENT_ID);
	}

	public static void setParentId(String parentId) {
		GlobalContext.setContext(GlobalContext.PARENT_ID, parentId);
	}

	public static String getModuleId() {
		return  ConfClient.getAppName() + "|" +	
				ConfigEnum.DEFAULT.get(Constant.IP_ADDRESS) + "|" +
				ConfigEnum.DEFAULT.get(Constant.SERVER_PORT_NAME);
	}
	//构造函数
	private static final InheritablePolarisThreadLocal<Map<String, String>> holder=new InheritablePolarisThreadLocal<Map<String,String>>(){
		@Override protected Map<String,String>initialValue(){
			return new HashMap<String,String>();
		}
	};

	public static void setContext(String key, String value) {
		Map<String, String> map = holder.get();
		map.put(key, value);
		holder.set(map);
	}
	public static String getContext(String key) {
		return holder.get().get(key);
	}
	public static void removeContext(String key) {
		holder.get().remove(key);
	}
	public static Map<String,String> getContext() {
		return holder.get();
	}
	public static void removeContext() {
		holder.remove();
	}
}
