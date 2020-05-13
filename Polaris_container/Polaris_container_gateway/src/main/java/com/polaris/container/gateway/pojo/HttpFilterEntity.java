package com.polaris.container.gateway.pojo;

import com.polaris.container.gateway.HttpFilter;

public class HttpFilterEntity {
	// 成员变量  
    private int order;  
    private String key;
    private HttpFilter filter;
    private HttpFile[] files;

    // 构造方法  
    public HttpFilterEntity(HttpFilter filter, String key, int order, HttpFile... files) {  
    	this.key = key;
        this.order = order; 
        this.files = files;
        this.setFilter(filter);
    }
	public HttpFilter getFilter() {
		return this.filter;
	}
	public void setFilter(HttpFilter filter) {
		this.filter = filter;
		this.filter.setHttpFilterEntity(this);
	}
	public String getKey() {
		return this.key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public void setOrder(Integer order) {
		this.order = order;
	}
	public Integer getOrder() {
		return order;
	}
	public HttpFile[] getFiles() {
		return files;
	}
	public void setFiles(HttpFile... files) {
		this.files = files;
	}
	public void setOrder(int order) {
		this.order = order;
	}
}
