package com.polaris.container.gateway;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.polaris.container.gateway.pojo.HttpFilterEntity;
import com.polaris.core.config.ConfClient;

import jodd.util.StringUtil;

public abstract class HttpFilterChain <T extends HttpFilter>{
	protected List<T> filters = new CopyOnWriteArrayList<>();
    public void add(T filter) {
    	filters.add(filter);
        Collections.sort(filters, new HttpFilterCompare());
    }
    public void remove(T filter) {
    	filters.remove(filter);
    }
    protected boolean skip(T filter) {
    	HttpFilterEntity httpFilterEntity = filter.getHttpFilterEntity();
    	if (httpFilterEntity == null) {
    		return true;
    	}
    	String key = httpFilterEntity.getKey();
    	if (StringUtil.isEmpty(key)) {
    		return true;
    	}
    	if (!HttpFilterConstant.ON.equals(ConfClient.get(key))) {
    		return true;
    	}
    	return false;
    }
}
