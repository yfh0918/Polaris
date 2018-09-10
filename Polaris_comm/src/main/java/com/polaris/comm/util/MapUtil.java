package com.polaris.comm.util;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

public class MapUtil {
	
	private static final LogUtil logger = LogUtil.getInstance(MapUtil.class);

	//根据key排序
	public static <K extends Comparable<? super K>, V > LinkedHashMap<K, V> sortByKey(Map<K, V> map) {
		LinkedHashMap<K, V> result = new LinkedHashMap<>();
        Stream<Entry<K, V>> st = map.entrySet().stream();
        st.sorted(Comparator.comparing(e -> e.getKey())).forEach(e -> result.put(e.getKey(), e.getValue()));
        return result;
    }	
		
	//根据value排序
	public static <K, V extends Comparable<? super V>> LinkedHashMap<K, V> sortByValue(Map<K, V> map) {
		LinkedHashMap<K, V> result = new LinkedHashMap<>();
        Stream<Entry<K, V>> st = map.entrySet().stream();
        st.sorted(Comparator.comparing(e -> e.getValue())).forEach(e -> result.put(e.getKey(), e.getValue()));
        return result;
    }
	
	//获取第一个元素
	public static <K, V> Entry<K, V> getHead(LinkedHashMap<K, V> map) {
	    return map.entrySet().iterator().next();
	}
	
	//获取最后一个元素
	@SuppressWarnings("unchecked")
	public static <K, V> Entry<K, V> getTail(LinkedHashMap<K, V> map) {
		try {
			Field tail = map.getClass().getDeclaredField("tail");
		    tail.setAccessible(true);
		    return (Entry<K, V>) tail.get(map);
		} catch (Exception ex) {
			logger.error(ex);
			return null;
		}
	}
}
