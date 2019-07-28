package com.polaris.core.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

/**
 * list相关处理的工具类
 * <p>
 * ListUtil.java
 * </p>
 * 
 * @author wxy
 * @version 1.0 2009-3-19
 */
public class ListUtil {
    private static LogUtil log =  LogUtil.getInstance(ListUtil.class);
    
    private ListUtil() {
    }

    /**
     * List是否为空的验证
     * 
     * @param parm
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static boolean isNullOrEmpty(List parm) {
        boolean rtn = false;
        if (null == parm || parm.isEmpty()) {
            rtn = true;
        }
        return rtn;
    }
    
    /**
     * List是否不为空的验证
     */
    @SuppressWarnings("rawtypes")
    public static boolean isNotEmpty(List parm) {
        return !isNullOrEmpty(parm);
    }

    /**
     * 查找objList不在baseList中的数据
     * 
     * @param <T>
     * @param baseList
     * @param objList
     * @author wxy
     * @return List<T> 返回等于objList的数据或者非空的List
     */
    public static <T> List<T> findNotIncludeValues(List<T> baseList, List<T> objList) {
        if (isNullOrEmpty(baseList)) {
            return objList;
        }
        if (isNullOrEmpty(objList)) {
            return new ArrayList<T>(0);
        }
        List<T> r = new ArrayList<T>(objList.size());
        HashMap<T, String> map = new HashMap<T, String>(baseList.size());
        for (T o : baseList) {
            map.put(o, "1");
        }
        for (T o : objList) {
            if (map.get(o) == null) {
                r.add(o);
            }
        }
        return r;
    }
    
    /**
     * 查找objList不在baseList中的数据
     * 
     * @param <T>
     * @param baseList
     * @param objList
     * @param idFieldName
     *            对象的主键fieldName
     * @author wxy
     * @return List<T> 返回等于objList的数据或者非空的List
     */
    public static <T> List<T> findNotIncludeValues(List<T> baseList, List<T> objList,
                                                   String idFieldName) {
        if (isNullOrEmpty(baseList)) {
            return objList;
        }
        if (isNullOrEmpty(objList)) {
            return new ArrayList<T>(0);
        }
        if (StringUtil.isEmpty(idFieldName)) {
            return findNotIncludeValues(baseList, objList);
        }
        List<T> r = new ArrayList<T>(objList.size());
        try {
            Method idMethod = baseList.get(0).getClass()
                .getMethod("get" + StringUtil.upperCaseFirstCharacter(idFieldName));

            HashMap<Object, String> map = new HashMap<Object, String>(baseList.size());
            for (T o : baseList) {
                map.put(idMethod.invoke(o), "1");
            }
            for (T o : objList) {
                if (map.get(idMethod.invoke(o)) == null) {
                    r.add(o);
                }
            }
        } catch (Exception e) {
            log.error("error,idFieldName is:" + idFieldName, e);
        }
        return r;
    }

    /**
     * 排除list中重复的记录
     * 
     * @param list
     * @return
     */
    public static <T> List<T> uniqueList(List<T> list) {
    	return list == null ? null : ImmutableSet.copyOf(list).asList();
    }

    /**
     * 将list按照指定长度分割为多个list
     * 
     * @param <T>
     * @param list
     * @param subListSize
     * @return
     */
    public static <T> List<List<T>> splitList(List<T> list, int subListSize) {
        List<List<T>> r = new ArrayList<List<T>>();
        if (isNullOrEmpty(list) || subListSize < 0) {
            r.add(list);
            return r;
        }
        int maxSize = subListSize;
        int subNum = list.size() / maxSize + 1;
        for (int i = 0; i < subNum; i++) {
            if (i * maxSize < list.size()) {
                List<T> subList = new ArrayList<T>(maxSize);
                int endIndex = (i + 1) * maxSize;
                endIndex = endIndex < list.size() ? endIndex : (list.size());
                subList.addAll(list.subList(i * maxSize, endIndex));

                r.add(subList);
            }
        }

        return r;
    }

    public static interface ListMegareOpr<T> {
        public boolean isNeedMegare(T t1, T t2);

        public void megareOpr(T t1, T t2);
    }
    
	public static String collection2String(Collection<String> list, String seprater){
		if (list == null || list.isEmpty()) {
			return "";
		} else {
			StringBuilder sb = new StringBuilder();
			Iterator<String> i = list.iterator();
			boolean first = true;
			while (i.hasNext()) {
				if (!first){
					sb.append(seprater);
				}
				sb.append(i.next());
				first = false;
			}
			return sb.toString();
		}
	}
	
    public static <T> void add2Map(Map<String, List<T>> map, String key, T value){
		List<T> list = map.get(key);
		if(list == null){
			list = new ArrayList<T>();
			map.put(key, list);
		}
		list.add(value);
    }
    
    public static <T> List<T> subList(List<T> lists, int endIndex) {
        return subList(lists, 0, endIndex);
    }

    public static <T> List<T> subList(List<T> lists, int startIndex, int endIndex) {
    	if (lists.isEmpty()) {
    		Lists.newArrayList();
    	} else {
            if (endIndex <= 0) {
                return Lists.newArrayList();
            } else if (endIndex > lists.size()) {
                endIndex = lists.size();
            }
            if (startIndex < 0) {
                startIndex = 0;
            }
            if (startIndex >= endIndex) {
                return Lists.newArrayList();
            }
            return lists.subList(startIndex, endIndex);
    	}
        return Lists.newArrayList();
    }
    
	//给List排序（list中只包含map）
    public static List<Map<String, Object>> ListSort(List<Map<String, Object>> sourceList, List<String> sortFields){
    	if (sourceList == null || sourceList.isEmpty() || sortFields == null || sortFields.isEmpty()) {
    		return sourceList;
    	}
    	List<String> tempList = new ArrayList<>();
    	for (String temp : sortFields) {
    		tempList.add(temp);
    	}
        Collections.sort(sourceList, new Comparator<Map<String, Object>>(){  
        	@Override
            public int compare(Map<String, Object> o1,Map<String, Object> o2){ 
            	return ListUtil.compare(tempList, o1, o2);
            }  
        });
        return sourceList;
    }
	private static int compare(List<String> sortFields, Map<String, Object> o1,Map<String, Object> o2){
		for (String field : sortFields) {
			String str1 = "";
			String str2 = "";
			if (o1.get(field) != null) {
				str1 = o1.get(field).toString();
			}
			if (o2.get(field) != null) {
				str2 = o2.get(field).toString();
			}
			if (StringUtils.isEmpty(str1) || StringUtils.isEmpty(str2)) {
				if (StringUtils.isEmpty(str1) && StringUtils.isEmpty(str2)) {
					if (sortFields.size() == 1) {
	    				return 0;
	    			}
	    			sortFields.remove(field);
	    			return compare(sortFields, o1, o2);
				}
				if (StringUtils.isEmpty(str1) && !StringUtils.isEmpty(str2)) {
					return -1;
				}
				if (StringUtils.isEmpty(str2) && !StringUtils.isEmpty(str1)) {
					return 1;
				}
			} else {
	    		if (str1.compareTo(str2) > 0) {
	    			return 1;
	    		}
	    		if (str1.compareTo(str2) < 0) {
	    			return -1;
	    		}
	    		if (str1.compareTo(str2) == 0) {
	    			if (sortFields.size() == 1) {
	    				return 0;
	    			}
	    			sortFields.remove(field);
	    			return compare(sortFields, o1, o2);
	    		}
			}
    	}
		return 0;
	}
}