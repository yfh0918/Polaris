package com.polaris.core;

import java.util.HashMap;
import java.util.Map;

import com.polaris.core.thread.InheritablePolarisThreadLocal;

public class GlobalContext {
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
