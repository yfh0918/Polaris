package com.polaris.workflow.util;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.*;

/**
 * 有序Properties
 */
public class LinkedProperties extends Properties {
    private static final long serialVersionUID = 1L;
    private Map<Object, Object> linkMap = new LinkedHashMap<>();

    @Override
    public void clear() {
        linkMap.clear();
    }
    
    @Override
    public boolean contains(Object value) {
        return linkMap.containsValue(value);
    }
    @Override
    public boolean containsKey(Object key) {
        return linkMap.containsKey(key);
    }
    @Override
    public boolean containsValue(Object value) {
        return linkMap.containsValue(value);
    }
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
	public Enumeration elements() {
        throw new RuntimeException("Method elements is not supported in LinkedProperties class");
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
	public Set entrySet() {
        return linkMap.entrySet();
    }

    @Override
    public boolean equals(Object o) {
        return linkMap.equals(o);
    }

    @Override
    public Object get(Object key) {
        return linkMap.get(key);
    }

    @Override
    public String getProperty(String key) {
        Object oval = get(key); //here the class Properties uses super.get()
        if (oval == null) {
        	return null;
        } else {
            return (oval instanceof String) ? (String) oval : null; //behavior of standard properties
        }
    }

    @Override
    public boolean isEmpty() {
        return linkMap.isEmpty();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
	public Enumeration keys() {
        Set keys = linkMap.keySet();
        return Collections.enumeration(keys);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
	public Set keySet() {
        return linkMap.keySet();
    }

    @Override
    public void list(PrintStream out) {
        this.list(new PrintWriter(out, true));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
	public void list(PrintWriter out) {
        out.println("-- listing properties --");
        for (Map.Entry e : (Set<Map.Entry>) this.entrySet()) {
            String key = (String) e.getKey();
            String val = (String) e.getValue();
            if (val.length() > 40) {
                val = val.substring(0, 37) + "...";
            }
            out.println(key + "=" + val);
        }
    }

    @Override
    public Object put(Object key, Object value) {
        return linkMap.put(key, value);
    }

    @Override
    public int size() {
        return linkMap.size();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
	public Collection values() {
        return linkMap.values();
    }

}