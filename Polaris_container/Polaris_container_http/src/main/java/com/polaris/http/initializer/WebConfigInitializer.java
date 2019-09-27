package com.polaris.http.initializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.ServletContextListener;

import com.polaris.core.config.DefaultRootConfig;

abstract public class WebConfigInitializer {

	private static Set<Class<?>> rootConfigs = new HashSet<>();
	private static Set<Class<?>> webConfigs = new HashSet<>();
	private static List<ServletContextListener> listeners = new ArrayList<>();
	private static List<InnerFilter> filters = new ArrayList<>();
	private static Set<String> filterSet = new HashSet<>();
	private static Map<String, String> initParameters = new HashMap<>();
	
	public static void loadRootConfig(Class<?>... clazzs) {
		if (clazzs != null) {
			for (Class<?> clazz : clazzs) {
				rootConfigs.add(clazz);
			}
		}
		rootConfigs.add(DefaultRootConfig.class);
	}
	public static void loadWebConfig(Class<?>... clazzs) {
		if (clazzs != null) {
			for (Class<?> clazz : clazzs) {
				webConfigs.add(clazz);
			}
		}
	}
	
	public static void loadListener(ServletContextListener listener) {
		listeners.add(listener);
	}
	public static void loadFilter(String filterName, Filter filter, String... urlPatterns) {
		if (filterSet.contains(filterName)) {
			return;
		}
		filterSet.add(filterName);
		filters.add(new InnerFilter(filterName, filter, urlPatterns));
	}
	public static void loadInitParameters(String key ,String value) {
		initParameters.put(key, value);
	}
	public static Class<?>[] getRootConfigs() {
		return rootConfigs.toArray(new Class[0]);
	}
	
	public static Class<?>[] getWebConfigs() {
		if (webConfigs.size() > 0) {
			return webConfigs.toArray(new Class[0]);
		}
		return null;
	}
	public static List<ServletContextListener> getListeners() {
		return listeners;
	}
	public static List<InnerFilter> getFilters() {
		return filters;
	}
	public static Map<String, String> getInitParameters() {
		return initParameters;
	}
	
	static public class InnerFilter {
		String filterName;
		Filter filter;
		String[] urlPatterns;
		InnerFilter(String filterName, Filter filter, String... urlPatterns) {
			this.filter = filter;
			this.filterName = filterName;
			this.urlPatterns = urlPatterns;
		}
		public String getFilterName() {
			return filterName;
		}
		public Filter getFilter() {
			return filter;
		}
		public String[] getUrlPatterns() {
			return urlPatterns;
		}
	}
}
