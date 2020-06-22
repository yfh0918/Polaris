package com.polaris.core.log;

import java.util.Map;

import org.apache.logging.log4j.ThreadContext;
import org.slf4j.spi.MDCAdapter;

public class XfliMDCAdapter implements MDCAdapter {

    @Override
    public void put(final String key, final String val) {
        ThreadContext.put(key, val);
    }

    @Override
    public String get(final String key) {
        return ThreadContext.get(key);
    }

    @Override
    public void remove(final String key) {
        ThreadContext.remove(key);
    }

    @Override
    public void clear() {
        ThreadContext.clearMap();
    }

    @Override
    public Map<String, String> getCopyOfContextMap() {
        return ThreadContext.getContext();
    }

    @Override
    @SuppressWarnings({"unchecked","rawtypes"}) // nothing we can do about this, restricted by SLF4J API
    public void setContextMap(final Map map) {
        ThreadContext.clearMap();
        for (final Map.Entry<String, String> entry : ((Map<String, String>) map).entrySet()) {
            ThreadContext.put(entry.getKey(), entry.getValue());
        }
    }

}
